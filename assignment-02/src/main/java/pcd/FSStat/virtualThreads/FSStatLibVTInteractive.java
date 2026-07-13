package pcd.FSStat.virtualThreads;

import pcd.FSStat.common.Report;
import pcd.FSStat.gui.FSStatTestGUI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class FSStatLibVTInteractive {

    private final ReentrantLock lock = new ReentrantLock();
    private ExecutorService executor;
    private volatile boolean isStopped = false;

    public void startAnalysis(String dir, long maxFS, int nb, FSStatTestGUI.StatListener listener) {

        Report report = new Report(maxFS, nb);
        executor = Executors.newVirtualThreadPerTaskExecutor();

        executor.submit(() -> {
            try {
                recursiveFileSearch(dir, report, executor, listener);
                listener.onComplete(report);
            } catch (Exception e) {
                listener.onError("Interrotto");
            } finally {
                executor.shutdown();
            }
        });
    }

    public void stop() {
        if (executor != null && !executor.isShutdown()) {
            isStopped = true;
            executor.shutdownNow();
        }
    }

    private void recursiveFileSearch(String dirPath, Report report, ExecutorService executor, FSStatTestGUI.StatListener listener) {
        if (isStopped || Thread.currentThread().isInterrupted()) {
            return;
        }

        File dir = new File(dirPath);
        File[] directoryListing = dir.listFiles();

        List<Future<?>> futures = new ArrayList<>();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (isStopped) {
                    return;
                }

                if (child.isDirectory()) {
                    Future<?> future = executor.submit(() -> {
                        recursiveFileSearch(child.getAbsolutePath(), report, executor, listener);
                    });
                    futures.add(future);
                }

                else if (child.isFile()) {
                    lock.lock();
                    try {
                        report.addFile(child.length());

                        if (report.getTotalFiles() % 100 == 0) {
                            listener.onProgress(report);
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        } else {
            System.err.println("Impossible to read: " + dirPath);
        }

        for (Future<?> task : futures) {
            try {
                task.get();
            } catch (Exception e) {
                if (e.getCause() instanceof InterruptedException || isStopped) {
                    return;
                }
                throw new RuntimeException(e);
            }
        }
    }
}
