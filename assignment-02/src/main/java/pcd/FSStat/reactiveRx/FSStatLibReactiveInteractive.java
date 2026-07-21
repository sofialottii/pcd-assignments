package pcd.FSStat.reactiveRx;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.FSStat.common.Report;
import pcd.FSStat.gui.FSStatTestGUI;

import java.io.File;

public class FSStatLibReactiveInteractive {

    private volatile boolean isStopped = false;

    public void startAnalysis(String dir, long maxFS, int nb, FSStatTestGUI.StatListener listener) {

        isStopped = false;
        Report report = new Report(maxFS, nb);
        Observable<Long> source = getColdStream(dir);

        source.subscribeOn(Schedulers.io()).subscribe(size -> {
                    if (isStopped) return;

                    report.addFile(size);
                    if (report.getTotalFiles() % 100 == 0) {
                        listener.onProgress(report);
                    }
                }, error -> {
                    listener.onError("Errore Observable: " + error.getMessage());
                },
                () -> {
                    if (!isStopped) {
                        listener.onComplete(report);
                    }
                }
        );
    }


    public void stop() {
        isStopped = true;
    }

    private Observable<Long> getColdStream(String dirPath) {
        return Observable.create(emitter -> {
            recursiveFileSearch(emitter, dirPath);
            if (!emitter.isDisposed()) {
                emitter.onComplete();
            }
        });

    }

    private void recursiveFileSearch(ObservableEmitter<Long> emitter, String dirPath) {
        if (isStopped || emitter.isDisposed()) {
            return;
        }
        try {
            File dir = new File(dirPath);
            if (!dir.isDirectory()) {
                return;
            }
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (isStopped) return;

                    if (child.isDirectory()) {
                        recursiveFileSearch(emitter, child.getAbsolutePath());
                    } else if(child.isFile()) {
                        emitter.onNext(child.length());
                    }
                }
            }
        } catch (Exception e) {
            emitter.onError(e);
        }
    }
}
