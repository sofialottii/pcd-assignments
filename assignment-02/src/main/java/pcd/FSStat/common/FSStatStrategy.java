package pcd.FSStat.common;


public interface FSStatStrategy {
    /**
     * Avvia la ricerca asincrona popolando il report.
     * Al termine (successo o stop), invoca la callback onComplete.
     */
    void startReport(String dir, long maxFS, int nb, Report report, Runnable onComplete);

    /** Interrompe immediatamente la ricerca */
    void stopReport();
}