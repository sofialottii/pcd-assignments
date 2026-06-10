package pcd.FSStat.reactiveRx;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class FSStatLibReactive {

    private static void recursiveFileSearch(FlowableEmitter<Long> emitter, String dirPath){
        try {
            File dir = new File(dirPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (child.isDirectory()) {
                        recursiveFileSearch(emitter, child.getAbsolutePath());
                    }else{
                        emitter.onNext(child.length());
                    }
                }
            } else {
                // Handle the case where dir is not really a directory.
                // Checking dir.isDirectory() above would not be sufficient
                // to avoid race conditions with another process that deletes
                // directories.
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("esco");
        }
    }

    private static Flowable<Long> getColdStream(String dirPath){
        Flowable<Long> source = Flowable.create(emitter -> {
            new Thread(() -> {
                recursiveFileSearch(emitter, dirPath);
                emitter.onComplete();
            }).start();
        }, BackpressureStrategy.BUFFER);

        return source;
    }

    public Report getFSReport(String dir, long maxFS, int nb){

        Report report = new Report(maxFS, nb);

        Flowable<Long> source = getColdStream(dir);

        source.onBackpressureBuffer(5_000, () -> {
            System.out.println("HELP!");
        }).subscribeOn(Schedulers.computation())
                .blockingSubscribe(size -> {
                            report.addFile(size);
                        },
                        error -> {
                            System.err.println("Errore nel flusso: " + error.getMessage());

                        }
                );
        return report;
    }
}
