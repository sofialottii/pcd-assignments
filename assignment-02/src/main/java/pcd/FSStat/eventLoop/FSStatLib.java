package pcd.FSStat.eventLoop;

import io.vertx.core.AsyncResult;
import io.vertx.core.CompositeFuture;
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

    public Future<Report> getFSReport(String dir, long maxFS, int nb) {

        Promise<Report> promise = Promise.promise();
        Report report = new Report(maxFS, nb);
        //TODO: gestire i report nel caso ricorsivo

        //leggiamo la lista di file e dir dentro la directory attuale
        Future<List<String>> fileList = fs.readDir(dir);

        fileList.onComplete((AsyncResult<List<String>> res) -> {

            List<Future<?>> futures = new ArrayList<>();

            for (String file : res.result()) {  //iteriamo i file

                Future<FileProps> fileProps = fs.props(file); //leggiamo le proprietà del file
                futures.add(fileProps);

                fileProps.onComplete((AsyncResult<FileProps> res2) -> {
                    report.addFile(res2.result().size());
                    //System.out.println(res2.result().size());

                    /*if (res2.result().isDirectory()) { //se il file è una directory: ricorsione
                        getFSReport(file, maxFS, nb);
                    } else { //if (res2.result().isRegularFile()) { //se è un file: chiama l'addFile
                        report.addFile(res2.result().size());
                    }*/
                });
            }

            Future.all(futures)
                    .onSuccess(r -> {
                        promise.complete(report);
                    });
        });


        return promise.future();
        //NON ASPETTA: da fixare. Bisogna fare in modo che aspetti

    }
}
