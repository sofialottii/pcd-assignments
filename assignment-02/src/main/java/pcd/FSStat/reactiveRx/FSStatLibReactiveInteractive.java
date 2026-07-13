package pcd.FSStat.reactiveRx;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import pcd.FSStat.common.Report;
import pcd.FSStat.gui.FSStatTestGUI;

import java.io.File;

public class FSStatLibReactiveInteractive {

    private volatile boolean isStopped = false;

    public void startAnalysis(String dir, long maxFS, int nb, FSStatTestGUI.StatListener listener) {

        isStopped = false;
        Report report = new Report(maxFS, nb);
        Flowable<Long> source = getColdStream(dir);

        source.onBackpressureBuffer(5_000, () -> {
            System.out.println("HELP!");
        }).subscribe(size -> {
                    if (isStopped) return;

                    report.addFile(size);
                    if (report.getTotalFiles() % 100 == 0) {
                        listener.onProgress(report);
                    }
                },
                error -> {
                    listener.onError("Errore Flowable: " + error.getMessage());
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

    private Flowable<Long> getColdStream(String dirPath) {
        Flowable<Long> source = Flowable.create(emitter -> {
            new Thread(() -> {
                recursiveFileSearch(emitter, dirPath);
                emitter.onComplete();
            }).start();
        }, BackpressureStrategy.BUFFER);

        return source;
    }

    private void recursiveFileSearch(FlowableEmitter<Long> emitter, String dirPath) {
        if (isStopped || emitter.isCancelled()) {
            return;
        }
        try {
            File dir = new File(dirPath);
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File child : directoryListing) {
                    if (isStopped || emitter.isCancelled()) return;

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
