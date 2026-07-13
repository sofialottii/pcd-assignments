package pcd.FSStat.reactiveRx;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import pcd.FSStat.common.Report;

import java.io.File;

public class FSStatLibReactiveInteractive {

    private boolean isStopped = false;

    public void getFSReport(String dir, long maxFS, int nb) {

        isStopped = false;
        Report report = new Report(maxFS, nb);

        Flowable<Long> source = getColdStream(dir);

        source.onBackpressureBuffer(5_000, () -> {
            System.out.println("HELP!");
        }).blockingSubscribe(report::addFile,
                error -> {
                    System.err.println("Flow error: " + error.getMessage());
                }
        );
    }

    public void stop() {

        isStopped = true;

    }

    private static Flowable<Long> getColdStream(String dirPath) {
        Flowable<Long> source = Flowable.create(emitter -> {
            new Thread(() -> {
                recursiveFileSearch(emitter, dirPath);
                emitter.onComplete();
            }).start();
        }, BackpressureStrategy.BUFFER);

        return source;
    }

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
}
