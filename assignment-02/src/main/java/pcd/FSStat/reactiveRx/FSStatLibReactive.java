package pcd.FSStat.reactiveRx;

import io.reactivex.rxjava3.core.*;
import pcd.FSStat.common.Report;

import java.io.File;


public class FSStatLibReactive {

    /**
     * This support method handle the recursions calling himself when it finds a directory.
     * When it finds a file emits on the current stream the length of the file
     * @param emitter
     * @param dirPath
     */
    private static void recursiveFileSearch(FlowableEmitter<Long> emitter, String dirPath) {
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
    private static Flowable<Long> getColdStream(String dirPath) {
        Flowable<Long> source = Flowable.create(emitter -> {
            new Thread(() -> {
                recursiveFileSearch(emitter, dirPath);
                emitter.onComplete();
            }).start();
        }, BackpressureStrategy.BUFFER);

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

        Flowable<Long> source = getColdStream(dir);

        source.onBackpressureBuffer(5_000, () -> {
            System.out.println("HELP!");
        }).blockingSubscribe(report::addFile,
                error -> {
                    System.err.println("Flow error: " + error.getMessage());
                }
        );
        return report;
    }
}
