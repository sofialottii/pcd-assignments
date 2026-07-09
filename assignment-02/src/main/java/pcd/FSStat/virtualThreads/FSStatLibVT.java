package pcd.FSStat.virtualThreads;

import pcd.FSStat.common.Report;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class FSStatLibVT {

    private final ReentrantLock lock = new ReentrantLock();

    public Future<Report> getFSReport(String dir, long maxFS, int nb) {

        Report report = new Report(maxFS, nb);

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        return executor.submit(() -> {
            try {
                recursiveFileSearch(dir, report, executor);
            } finally {
                executor.shutdown();
            }
            return report;
        });


    }

    private void recursiveFileSearch(String dirPath, Report report, ExecutorService executor) {

        File dir = new File(dirPath);
        File[] directoryListing = dir.listFiles();

        List<Future<?>> futures = new ArrayList<>();

        if (directoryListing != null) {
            for (File child : directoryListing) {
                if (child.isDirectory()) {
                    Future<?> future = executor.submit(() -> {
                        recursiveFileSearch(child.getAbsolutePath(), report, executor);
                    });
                    futures.add(future);
                }

                else if (child.isFile()) {
                    lock.lock();
                    try {
                        report.addFile(child.length());
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
                throw new RuntimeException(e);
            }
        }

    }
}
