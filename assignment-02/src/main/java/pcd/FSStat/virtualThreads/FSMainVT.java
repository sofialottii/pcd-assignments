package pcd.FSStat.virtualThreads;

public class FSMainVT {
    public static void main(String[] args) {

        final String dir = "./";
        final long maxFS = 1000;
        final int nb = 10;

        FSStatLibVT lib = new FSStatLibVT();

        System.out.println(lib.getFSReport(dir, maxFS, nb).toString());
    }
}
