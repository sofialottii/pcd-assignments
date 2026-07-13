package pcd.FSStat.eventLoop;

import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.file.FileSystem;
import pcd.FSStat.gui.FSStatTestGUI;

public class FSStatEventLoopInteractive extends VerticleBase {

    private final String dir;
    private final long maxFS;
    private final int nb;
    private final FSStatTestGUI.StatListener listener;
    private FSStatLibInteractive lib;

    public FSStatEventLoopInteractive(String dir, long maxFS, int nb, FSStatTestGUI.StatListener listener) {
        this.dir = dir;
        this.maxFS = maxFS;
        this.nb = nb;
        this.listener = listener;
    }

    public Future<?> start() throws Exception {

        FileSystem fs = this.vertx.fileSystem();

        lib = new FSStatLibInteractive(fs);

        lib.startAnalysis(dir, maxFS, nb, listener);

        return super.start();
    }

    public Future<?> stop() throws Exception {
        if (lib != null) {
            lib.stop();
        }
        return super.stop();
    }
}

