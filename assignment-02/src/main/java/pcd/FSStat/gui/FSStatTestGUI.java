package pcd.FSStat.gui;

import io.vertx.core.Vertx;
import pcd.FSStat.common.Report;
import pcd.FSStat.eventLoop.FSStatEventLoop;
import pcd.FSStat.eventLoop.FSStatEventLoopInteractive;
import pcd.FSStat.eventLoop.FSStatLibInteractive;
import pcd.FSStat.reactiveRx.FSStatLibReactiveInteractive;
import pcd.FSStat.virtualThreads.FSStatLibVTInteractive;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Random;

public class FSStatTestGUI extends JFrame {

    private FSStatLibVTInteractive vtLib;
    private FSStatLibReactiveInteractive rxLib;
    private Vertx vertx;

    //componenti stato applicazione
    private File selectedDirectory;
    private String activeStrategy;

    //componenti UI
    private JButton btnStart;
    private JButton btnStop;
    private JButton btnBrowse;
    private JLabel lblSelectedDir;
    private JComboBox<String> comboStrategy;
    private BarChartPanel chartPanel;
    private JTextField txtMaxFS;
    private JTextField txtBands;
    //private JLabel lblStatus;

    /*
    public interface StatListener {
        void onProgress(Report currentReport);
        void onComplete(Report finalReport);
        void onError(String message);
    }
     */


    public FSStatTestGUI() {

        //finestra principale
        setTitle("FSStat GUI");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        selectedDirectory = new File(System.getProperty("user.dir"));

        //pannello superiore (grafico)
        chartPanel = new BarChartPanel();
        chartPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                "Distribuzione Dimensioni File",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14)
        ));
        add(chartPanel, BorderLayout.CENTER);

        //pannello inferiore
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 246, 250));

        JPanel configPanel = new JPanel(new GridBagLayout());
        configPanel.setOpaque(false);
        configPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        //directory
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.1;
        configPanel.add(new JLabel("Cartella:"), gbc);
        btnBrowse = new JButton("Esplora file...");
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.2;
        configPanel.add(btnBrowse, gbc);
        lblSelectedDir = new JLabel(selectedDirectory.getAbsolutePath());
        lblSelectedDir.setFont(new Font("Arial", Font.ITALIC, 12));
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.7;
        configPanel.add(lblSelectedDir, gbc);

        //parametri MaxFS e NB
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.1;
        configPanel.add(new JLabel("MaxFS (MB):"), gbc);

        txtMaxFS = new JTextField("100"); //100 MB di default
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0.2;
        configPanel.add(txtMaxFS, gbc);

        gbc.gridx = 2; gbc.gridy = 1; gbc.weightx = 0.7;
        JPanel bandsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bandsPanel.setOpaque(false);
        bandsPanel.add(new JLabel("Numero Bande:"));
        txtBands = new JTextField("10", 5);
        bandsPanel.add(txtBands);
        bandsPanel.add(new JLabel("Strategia:"));

        comboStrategy = new JComboBox<>(new String[]{"Virtual Threads", "RxJava", "Vert.x"});
        bandsPanel.add(comboStrategy);
        configPanel.add(bandsPanel, gbc);

        bottomPanel.add(configPanel, BorderLayout.NORTH);

        //pulsanti
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));

        btnStop = new JButton("Stop");
        btnStop.setBackground(new Color(231, 76, 60)); // Rosso
        btnStop.setForeground(Color.WHITE);
        btnStop.setFont(new Font("Arial", Font.BOLD, 14));
        btnStop.setPreferredSize(new Dimension(140, 40));
        btnStop.setFocusPainted(false);
        btnStop.setEnabled(false); //spento all'avvio

        btnStart = new JButton("Start");
        btnStart.setBackground(new Color(46, 204, 113)); // Verde
        btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));
        btnStart.setPreferredSize(new Dimension(180, 40));
        btnStart.setFocusPainted(false);

        buttonPanel.add(btnStop);
        buttonPanel.add(btnStart);
        //buttonPanel.add(lblStatus);

        bottomPanel.add(buttonPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        //gestione dei listener per i bottoni
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(selectedDirectory);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedDirectory = fc.getSelectedFile();
                lblSelectedDir.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnStart.addActionListener(e -> startAnalysis());
        btnStop.addActionListener(e -> stopAnalysis());
    }


    private void startAnalysis() {
        if (selectedDirectory == null || !selectedDirectory.exists()) {
            JOptionPane.showMessageDialog(this, "Seleziona una directory qualsiasi per avviare il test visivo!", "Avviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        long maxFS;
        int nb;
        try {
            maxFS = Long.parseLong(txtMaxFS.getText().trim()) * 1024 * 1024;
            nb = Integer.parseInt(txtBands.getText().trim());
            if (maxFS <= 0 || nb <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Parametri non validi!", "Errore Parametri", JOptionPane.ERROR_MESSAGE);
            return;
        }

        activeStrategy = comboStrategy.getSelectedItem().toString();
        setUiSearchingState(true);
        //lblStatus.setText(" Scansione in corso con " + activeStrategy + "...");

        //cancello grafico vecchio
        chartPanel.updateReport(new Report(maxFS, nb));

        //diciamo alla GUI cosa fare quando le librerie inviano dati
        /*StatListener uiListener = new StatListener() {
            @Override
            public void onProgress(Report report) {
                // Impacchettiamo per il Thread della Grafica (Swing)
                SwingUtilities.invokeLater(() -> chartPanel.updateReport(report));
            }

            @Override
            public void onComplete(Report finalReport) {
                SwingUtilities.invokeLater(() -> {
                    chartPanel.updateReport(finalReport);
                    lblStatus.setText(" Completato!");
                    setUiSearchingState(false);
                });
            }

            @Override
            public void onError(String message) {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText(" Errore: " + message);
                    setUiSearchingState(false);
                });
            }
        };*/

        //avvio della libreria scelta
        if ("Virtual Threads".equals(activeStrategy)) {
            vtLib = new FSStatLibVTInteractive();
        } else if ("RxJava".equals(activeStrategy)) {
            rxLib = new FSStatLibReactiveInteractive();
        } else {
            vertx = Vertx.vertx();
            vertx.deployVerticle(new FSStatEventLoop(selectedDirectory.getAbsolutePath(), maxFS, nb));
        }
    }

    private void stopAnalysis() {
        if ("Virtual Threads".equals(activeStrategy) && vtLib != null) {
            vtLib.stop();
        } else if ("RxJava".equals(activeStrategy)) {
            rxLib.stop();
        } else {
            /*vertx.close(res -> {
                SwingUtilities.invokeLater(() -> {
                    lblStatus.setText(" Vert.x arrestato.");
                    setUiSearchingState(false);
                });
            });
        }*/
            vertx.close(); //se decommenti sopra, commenta qui
        }

        if (!"Vert.x".equals(activeStrategy)) {
            setUiSearchingState(false);
            //lblStatus.setText(" Interrotto dall'utente.");
        }
    }

    private void setUiSearchingState(boolean isSearching) {
        btnStart.setEnabled(!isSearching);
        btnBrowse.setEnabled(!isSearching);
        comboStrategy.setEnabled(!isSearching);
        txtMaxFS.setEnabled(!isSearching);
        txtBands.setEnabled(!isSearching);
        btnStop.setEnabled(isSearching);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FSStatTestGUI().setVisible(true));
    }

    //pannello per il grafico
    private static class BarChartPanel extends JPanel {
        private Report report;

        public BarChartPanel() {
            setBackground(Color.WHITE);
        }

        public void updateReport(Report report) {
            this.report = report;
            repaint();
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
            if (snapshot.isEmpty()) return;

            int numBands = snapshot.size();
            int maxFiles = snapshot.values().stream().max(Integer::compare).orElse(0);

            int paddingLeftRight = 60; //prima era 50
            int paddingTopBottom = 60;
            int availableWidth = w - (paddingLeftRight * 2);
            int availableHeight = h - (paddingTopBottom * 2);

            int barWidth = (int) ((availableWidth / numBands) * 0.7);
            int spacing = (int) ((availableWidth / numBands) * 0.3);

            int index = 0;
            for (Map.Entry<Long, Integer> entry : snapshot.entrySet()) {
                int count = entry.getValue();

                //calcolo dell'altezza della colonna in proporzione al valore più alto registrato
                int barHeight = (maxFiles > 0) ? (int) (((double) count / maxFiles) * availableHeight) : 0;
                int x = paddingLeftRight + index * (barWidth + spacing) + (spacing / 2);
                int y = h - paddingTopBottom - barHeight;

                //calcolo HUE SHIFT
                float hue = ((float) index / (numBands - 1)) * 0.8f;
                Color barColor = Color.getHSBColor(hue, 0.85f, 0.9f);

                //disegno corpo colonna
                g2d.setColor(barColor);
                g2d.fillRect(x, y, barWidth, barHeight);

                //disegno bordo nero spesso
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(x, y, barWidth, barHeight);

                //font labels
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                long size = entry.getKey();
                String label = (size == Long.MAX_VALUE) ? "Out" :
                        (size >= 1024*1024) ? (size/(1024*1024))+"MB" :
                        (size >= 1024) ? (size/1024)+"KB" : size+"B";

                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(label, x + (barWidth - fm.stringWidth(label)) / 2, h - paddingLeftRight + 18);

                g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2d.drawString(String.valueOf(count), x + (barWidth - fm.stringWidth(String.valueOf(count))) / 2, y - 6);
                index++;
            }

            //disegno asse di base orizzontale
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(4));
            g2d.drawLine(paddingLeftRight, h - paddingTopBottom, w - paddingLeftRight, h - paddingTopBottom);
        }
    }
}