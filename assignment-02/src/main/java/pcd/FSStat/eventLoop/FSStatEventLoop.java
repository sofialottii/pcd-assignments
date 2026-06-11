package pcd.FSStat.eventLoop;


import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.file.FileSystem;
import pcd.FSStat.common.Report;


public class FSStatEventLoop extends VerticleBase {

    private final String dir;
    private final long maxFS;
    private final int nb;

    public FSStatEventLoop(String dir, long maxFS, int nb) {
        this.dir = dir;
        this.maxFS = maxFS;
        this.nb = nb;
    }

    public Future<?> start() throws Exception {

        FileSystem fs = this.vertx.fileSystem();

        FSStatLib lib = new FSStatLib(fs);

        Future<Report> report = lib.getFSReport(dir, maxFS, nb);

        report.onComplete(( res) -> {
            System.out.println(res.result().toString());

            this.vertx.close();
        });

        return super.start();
    }
}
