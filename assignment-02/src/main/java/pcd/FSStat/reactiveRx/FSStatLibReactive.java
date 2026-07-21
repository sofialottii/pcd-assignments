package pcd.FSStat.reactiveRx;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pcd.FSStat.common.Report;

import java.io.File;


public class FSStatLibReactive {

    /**
     * This support method handle the recursions calling himself when it finds a directory.
     * When it finds a file emits on the current stream the length of the file
     * @param emitter
     * @param dirPath
     */
    private static void recursiveFileSearch(ObservableEmitter<Long> emitter, String dirPath) {
        try {
            File dir = new File(dirPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (child.isDirectory()) {
                        recursiveFileSearch(emitter, child.getAbsolutePath());
                    } else {
                        emitter.onNext(child.length());
                    }
                }
            } else {
                emitter.onError(new Throwable(dir + "is not a directory"));
            }
        } catch (Exception e) {
            emitter.onError(e);
        }
    }

    /**
     * Prepares a cold Stream using a Flowable
     * @param dirPath the path
     * @return a Flowable which contains the size of the files
     */
    private static Observable<Long> getColdStream(String dirPath) {

        Observable<Long> source = Observable.create(emitter -> {
            recursiveFileSearch(emitter, dirPath);
            emitter.onComplete();

        });

        return source;
    }

    /**
     * Create a report by collecting data from the stream
     * @param dir directory
     * @param maxFS max value of the file size range [0, maxFS]
     * @param nb number of file size bands
     * @return report completed
     */
    public Report getFSReport(String dir, long maxFS, int nb) {

        Report report = new Report(maxFS, nb);

        Observable<Long> source = getColdStream(dir);

        source.blockingSubscribe(report::addFile);

        /*source.onBackpressureBuffer(5_000, () -> {
            System.out.println("HELP!");
        }).blockingSubscribe(report::addFile,
                error -> {
                    System.err.println("Flow error: " + error.getMessage());
                }
        );*/


        return report;
    }
}

/**
 * domande a cui dobbiamo trovare risposta:
 *
 * * differenza tra subscribeOn e observeOn, e se vengono usati solo con schedulers
 * * se creare nuovo thread è sbagliato e bisogna lavorare direttamente con schedulers
 * * se lavorare con schedulers significa automaticamente cold stream e non hot stream, o se sono cose completamente diverse
 *
 *
 */
