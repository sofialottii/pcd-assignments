package pcd.FSStat.gui;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Random;

public class FSStatTestGUI extends JFrame {

    // Campi di stato per la simulazione temporanea
    private TemporaryReport currentReport;
    private Thread mockSearchThread;

    // SOLUZIONE 1: Utilizziamo esplicitamente java.util.Timer per evitare conflitti con javax.swing.Timer
    private java.util.Timer updateTimer;

    private File selectedDirectory;
    private volatile boolean stopRequested = false;

    // Componenti UI
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnBrowse;
    private JLabel lblSelectedDir;
    private JComboBox<String> comboStrategy;
    private BarChartPanel chartPanel;

    public FSStatTestGUI() {
        // Configurazione Finestra Principale
        setTitle("FSStat Analyzer - TEST STANDALONE GUI");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // 1. ZONA GRAFICO (Occupa il 60% circa dell'altezza totale)
        chartPanel = new BarChartPanel();
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(15, 15, 0, 15);
        add(chartPanel, gbc);

        // 2. ZONA CONTROLLI (Inferiore, occupa il restante 40%)
        JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Riga 1: Selezione Directory
        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBrowse = new JButton("Esplora file...");
        lblSelectedDir = new JLabel("Nessuna directory selezionata (Usa una cartella di test)");
        lblSelectedDir.setFont(new Font("Arial", Font.ITALIC, 12));
        dirPanel.add(btnBrowse);
        dirPanel.add(lblSelectedDir);

        // Riga 2: Selezione Paradigma (Estetico in questo test standalone)
        JPanel strategyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        strategyPanel.add(new JLabel("Seleziona Paradigma Concorrente: "));
        comboStrategy = new JComboBox<>(new String[]{"[SIMULAZIONE] Virtual Threads", "[SIMULAZIONE] RxJava", "[SIMULAZIONE] Vert.x"});
        strategyPanel.add(comboStrategy);

        // Riga 3: Pulsanti Start / Stop coordinati come da mock-up
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));

        btnStop = new JButton("Stop");
        btnStop.setBackground(new Color(231, 76, 60)); // Rosso
        btnStop.setForeground(Color.WHITE);
        btnStop.setFont(new Font("Arial", Font.BOLD, 14));
        btnStop.setFocusPainted(false);
        btnStop.setEnabled(false); // Spento all'avvio

        btnStart = new JButton("Start");
        btnStart.setBackground(new Color(46, 204, 113)); // Verde
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStart.setFocusPainted(false);

        buttonPanel.add(btnStop);
        buttonPanel.add(btnStart);

        controlsPanel.add(dirPanel);
        controlsPanel.add(strategyPanel);
        controlsPanel.add(buttonPanel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(controlsPanel, gbc);

        // --- Gestione Listener degli Eventi ---
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedDirectory = fc.getSelectedFile();
                lblSelectedDir.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnStart.addActionListener(e -> startMockAnalysis());
        btnStop.addActionListener(e -> stopMockAnalysis());
    }

    /**
     * Simula una ricerca concorrente riempiendo progressivamente il report
     */
    private void startMockAnalysis() {
        if (selectedDirectory == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una directory qualsiasi per avviare il test visivo!", "Avviso", JOptionPane.INFORMATION_MESSAGE);
        }

        long maxFS = 600 * 1024 * 1024L; // 600 MB
        int nb = 6;
        currentReport = new TemporaryReport(maxFS, nb);
        stopRequested = false;

        setUiSearchingState(true);

        // Creiamo un thread in background per simulare l'arrivo asincrono di dati di file
        mockSearchThread = new Thread(() -> {
            Random random = new Random();
            // Simula la scoperta di 400 file ad intervalli casuali
            for (int i = 0; i < 400 && !stopRequested; i++) {
                // Genera dimensioni di file casuali da 0 a 700 MB (per toccare anche l'Out of Range)
                long simulatedSize = (long) (random.nextDouble() * 700 * 1024 * 1024L);
                currentReport.addFile(simulatedSize);

                try {
                    Thread.sleep(10 + random.nextInt(20)); // Ritardo per l'effetto progressivo sulla GUI
                } catch (InterruptedException e) {
                    break;
                }
            }
            // Quando il thread finisce, notifica la GUI nel thread grafico principale
            SwingUtilities.invokeLater(this::finishMockAnalysis);
        });
        mockSearchThread.start();

        // SOLUZIONE 1: Istanziamo correttamente java.util.Timer con il costruttore vuoto standard
        updateTimer = new java.util.Timer(true);
        updateTimer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
                // Rimandiamo l'aggiornamento grafico sul thread corretto EDT di Swing
                SwingUtilities.invokeLater(() -> chartPanel.updateReport(currentReport));
            }
        }, 0, 80); // Refresh visivo ogni 80ms
    }

    private void stopMockAnalysis() {
        stopRequested = true;
        if (mockSearchThread != null) {
            mockSearchThread.interrupt();
        }
        finishMockAnalysis();
    }

    private void finishMockAnalysis() {
        // SOLUZIONE 1: Il metodo cancel() interrompe correttamente il java.util.Timer
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        setUiSearchingState(false);
        chartPanel.updateReport(currentReport); // Disegno finale statico completo
    }

    private void setUiSearchingState(boolean isSearching) {
        btnStart.setEnabled(!isSearching);
        btnBrowse.setEnabled(!isSearching);
        comboStrategy.setEnabled(!isSearching);
        btnStop.setEnabled(isSearching);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FSStatTestGUI().setVisible(true));
    }

    // --- Componente Grafico Custom interno per il Test ---
    private static class BarChartPanel extends JPanel {
        private TemporaryReport report;

        public BarChartPanel() {
            setBackground(Color.WHITE);
        }

        public void updateReport(TemporaryReport report) {
            this.report = report;
            repaint(); // Dice a Swing di ridisegnare richiamando paintComponent
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (report == null) return;

            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();

            NavigableMap<Long, Integer> snapshot = report.getBandsSnapshot();
            int numBands = snapshot.size();
            if (numBands == 0) return;

            // Trova il massimo attuale delle bande per gestire il ridimensionamento al 100% dell'altezza disponibile
            int maxFiles = snapshot.values().stream().max(Integer::compare).orElse(0);

            int paddingLeftRight = 50;
            int paddingTopBottom = 60;
            int availableWidth = w - (paddingLeftRight * 2);
            int availableHeight = h - (paddingTopBottom * 2);

            int barWidth = (int) ((availableWidth / numBands) * 0.7);
            int spacing = (int) ((availableWidth / numBands) * 0.3);

            int index = 0;
            for (Map.Entry<Long, Integer> entry : snapshot.entrySet()) {
                int count = entry.getValue();

                // Calcolo dell'altezza della colonna in proporzione al valore più alto registrato
                int barHeight = (maxFiles > 0) ? (int) (((double) count / maxFiles) * availableHeight) : 0;

                int x = paddingLeftRight + index * (barWidth + spacing) + (spacing / 2);
                int y = h - paddingTopBottom - barHeight;

                // Calcolo HUE SHIFT (Da Rosso [0.0f] a Viola/Rosato [0.8f])
                float hue = ((float) index / (numBands - 1)) * 0.8f;
                Color barColor = Color.getHSBColor(hue, 0.85f, 0.9f);

                // Disegno corpo colonna
                g2d.setColor(barColor);
                g2d.fillRect(x, y, barWidth, barHeight);

                // Disegno bordo nero spesso
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(x, y, barWidth, barHeight);

                // Scrittura delle etichette descrittive inferiori
                g2d.setFont(new Font("Arial", Font.PLAIN, 12));
                String labelMb = (entry.getKey() == Long.MAX_VALUE) ? "Out" : (entry.getKey() / (1024 * 1024)) + "M";

                g2d.drawString(labelMb, x + (barWidth / 5), h - paddingTopBottom + 25);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString(String.valueOf(count), x + (barWidth / 5), h - paddingTopBottom + 45);

                index++;
            }

            // Disegno asse di base orizzontale
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(paddingLeftRight, h - paddingTopBottom, w - paddingLeftRight, h - paddingTopBottom);
        }
    }

    // --- Classe Report interna autocontenuta (Copia Thread-Safe per il test) ---
    private static class TemporaryReport {
        private int totalFiles = 0;
        private final NavigableMap<Long, Integer> bands = new TreeMap<>();

        public TemporaryReport(long maxFS, int nb) {
            long chunkSize = maxFS / nb;
            for (int i = 0; i < nb; i++) {
                long endInterval = (i == nb - 1) ? maxFS : (i + 1) * chunkSize;
                this.bands.put(endInterval, 0);
            }
            this.bands.put(Long.MAX_VALUE, 0);
        }

        public synchronized void addFile(long size) {
            this.totalFiles++;
            Long cell = this.bands.ceilingKey(size);
            if (cell != null) {
                this.bands.put(cell, bands.get(cell) + 1);
            } else {
                this.bands.put(Long.MAX_VALUE, this.bands.get(Long.MAX_VALUE) + 1);
            }
        }

        public synchronized NavigableMap<Long, Integer> getBandsSnapshot() {
            return new TreeMap<>(this.bands);
        }
    }
}