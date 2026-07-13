package pcd.FSStat.eventLoop;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.file.FileSystem;
import pcd.FSStat.common.Report;
import pcd.FSStat.gui.FSStatTestGUI;

import java.util.ArrayList;
import java.util.List;

public class FSStatLibInteractive {

    private final FileSystem fs;
    private volatile boolean isStopped = false;

    public FSStatLibInteractive(FileSystem fs) {
        this.fs = fs;
    }

    public void startAnalysis(String dir, long maxFS, int nb, FSStatTestGUI.StatListener listener) {

        isStopped = false;
        Report report = new Report(maxFS, nb);

        getFSRecursiveReport(dir, report, listener).onComplete(res -> {
            if (isStopped) {
                return;
            }
            if(res.succeeded()){
                listener.onComplete(report);
            }
            else {
                listener.onError(res.cause().getMessage());
            }

        });
    }

    public void stop() {
        isStopped = true;
    }

    private Future<Void> getFSRecursiveReport(String dir, Report report, FSStatTestGUI.StatListener listener){
        Promise<Void> promise = Promise.promise();

        if (isStopped) {
            promise.complete();
            return promise.future();
        }

        //leggiamo la lista di file e dir dentro la directory attuale
        Future<List<String>> fileList = fs.readDir(dir);

        fileList.onComplete((AsyncResult<List<String>> resDir) -> {
            if (isStopped) {
                promise.complete();
                return;
            }

            if (resDir.failed()){
                promise.fail(resDir.cause());
                return;
            }

            List<Future<?>> futures = new ArrayList<>();

            for (String file : resDir.result()) {
                if (isStopped) break;

                Promise<Void> fileProps = Promise.promise();//leggiamo le proprietà del file
                futures.add(fileProps.future());

                fs.props(file).onComplete((resProps) -> {
                    if (resProps.failed()){
                        fileProps.fail(resProps.cause());
                        return;
                    }

                    if (resProps.result().isDirectory()){
                        getFSRecursiveReport(file, report, listener).onComplete(resRec -> {
                            if (resRec.succeeded()){
                                fileProps.complete();
                            }
                            else {
                                fileProps.fail(resRec.cause());
                            }
                        });
                    }
                    else{
                        report.addFile(resProps.result().size());
                        if (report.getTotalFiles() % 100 == 0) {
                            listener.onProgress(report);
                        }
                        fileProps.complete();
                    }

                });
            }

            Future.all(futures).onComplete(resAll -> {
                if(resAll.succeeded()){
                    promise.complete();
                }
                else {
                    promise.fail(resAll.cause());
                }

            });
        });
        return promise.future();
    }
}
