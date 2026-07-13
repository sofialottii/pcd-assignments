package pcd.FSStat.common;

import java.util.NavigableMap;
import java.util.TreeMap;

public class Report {

    private static final long OUT_OF_RANGE = Long.MAX_VALUE;

    private int totalFiles;
    private final NavigableMap<Long, Integer> bands = new TreeMap<>();

    public Report(long maxFS, int nb) {
        this.totalFiles = 0;
        long chunkSize = maxFS / nb;

        for (int i = 0; i < nb; i++) {
            long endInterval = (i == nb - 1) ? maxFS : (i + 1) * chunkSize;
            this.bands.put(endInterval, 0);
        }

        this.bands.put(OUT_OF_RANGE, 0);

    }

    public void addFile(long size) {
        //increase the number of files viewed
        this.totalFiles++;

        //add the file to the corresponding band
        Long cell = this.bands.ceilingKey(size);

        if (cell != null) {
            this.bands.put(cell, bands.get(cell)+1);
        } else {
            this.bands.put(OUT_OF_RANGE, this.bands.get(OUT_OF_RANGE)+1);
        }
    }

    @Override
    public String toString() {
        String res = "Total files founded: " + this.totalFiles + "\n";
        res += "Distribution:\n";

        for (Long key : this.bands.keySet()) {
            int numFile = this.bands.get(key);

            if (key == OUT_OF_RANGE) {
                res += " - Out of range: " + numFile + " files\n";
            } else {
                res += " - Up to " + key + " bytes: " + numFile + " files\n";
            }
        }

        return res;
    }
    public synchronized int getTotalFiles() {
        return this.totalFiles;
    }

    // Ritorna una copia ordinata delle bande per evitare problemi di concorrenza durante la lettura
    public synchronized NavigableMap<Long, Integer> getBandsSnapshot() {
        return new TreeMap<>(this.bands);
    }
}
