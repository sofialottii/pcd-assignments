package pcd.FSStat.eventLoop;

import io.vertx.core.AsyncResult;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;

import java.util.ArrayList;
import java.util.List;


public class FSStatLib {

    FileSystem fs;

    public FSStatLib(FileSystem fs) {
        this.fs = fs;

    }

    /**
     * Funzione d'appoggio ricorsiva, non ritorna nulla se non un segnale
     * per informare la funzione principale che l'operazione abbia avuto successo o meno.
     * Nel frattempo popola l'oggetto report
     * @param dir
     * @param report
     * @return
     */
    private Future<Void> getFSRecursiveReport(String dir, Report report){
        Promise<Void> promise = Promise.promise();

        //leggiamo la lista di file e dir dentro la directory attuale
        Future<List<String>> fileList = fs.readDir(dir);

        fileList.onComplete((AsyncResult<List<String>> resDir) -> {
            if (resDir.failed()){
                promise.fail(resDir.cause());
                return;
            }

            List<Future<?>> futures = new ArrayList<>();

            for (String file : resDir.result()) {  //iteriamo i file

                Promise<Void> fileProps = Promise.promise();//leggiamo le proprietà del file
                futures.add(fileProps.future());

                fs.props(file).onComplete((resProps) -> {
                   if (resProps.failed()){
                       fileProps.fail(resProps.cause());
                   }

                   if (resProps.result().isDirectory()){
                       getFSRecursiveReport(file, report).onComplete(resRec -> {
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

    public Future<Report> getFSReport(String dir, long maxFS, int nb) {

        Promise<Report> promise = Promise.promise();
        Report report = new Report(maxFS, nb);

        getFSRecursiveReport(dir, report).onComplete(res -> {
            if(res.succeeded()){
                promise.complete(report);
            }
            else {
                promise.fail(res.cause());
            }

        });


        return promise.future();


    }
}
