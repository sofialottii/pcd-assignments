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
                    if (emitter.isDisposed()) {
                        return;
                    }
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

        return Observable.create(emitter -> {
            recursiveFileSearch(emitter, dirPath);
            if (!emitter.isDisposed()) {
                emitter.onComplete();
            }
        });
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

        source.subscribeOn(Schedulers.io()).blockingSubscribe(report::addFile);

        return report;
    }
}

