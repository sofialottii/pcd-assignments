package pcd.FSStat.virtualThreads;

import pcd.FSStat.common.Report;
import java.util.concurrent.Future;

public class FSMainVT {
    public static void main(String[] args) {

        final String dir = "./";
        final long maxFS = 1000;
        final int nb = 10;

        FSStatLibVT lib = new FSStatLibVT();

        Future<Report> futureReport = lib.getFSReport(dir, maxFS, nb);

        try {
            Report report = futureReport.get();
            System.out.println(report.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
