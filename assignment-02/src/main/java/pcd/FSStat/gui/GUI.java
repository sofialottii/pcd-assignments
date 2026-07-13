package pcd.FSStat.gui;

import pcd.FSStat.common.FSStatStrategy;
import pcd.FSStat.common.Report;


import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
/*
public class FSStatMultiGUI extends JFrame {

    private FSStatStrategy currentStrategy;
    private Report currentReport;
    private Timer updateTimer;
    private File selectedDirectory;

    // Componenti di controllo e grafici
    private JButton btnStart, btnStop, btnBrowse;
    private JLabel lblSelectedDir;
    private JComboBox<String> comboStrategy;
    private BarChartPanel chartPanel; // Pannello di disegno creato nel passaggio precedente

    public FSStatMultiGUI() {
        setTitle("FSStat Analyzer - Multi Paradigm Unified GUI");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        // 1. ZONA GRAFICO (Superiore)
        chartPanel = new BarChartPanel();
        chartPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; gbc.weighty = 0.6;
        gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(15, 15, 0, 15);
        add(chartPanel, gbc);

        // 2. ZONA CONTROLLI (Inferiore)
        JPanel controlsPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // Riga Selezione Directory
        JPanel dirPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnBrowse = new JButton("Esplora file...");
        lblSelectedDir = new JLabel("Nessuna directory selezionata");
        lblSelectedDir.setFont(new Font("Arial", Font.ITALIC, 12));
        dirPanel.add(btnBrowse); dirPanel.add(lblSelectedDir);

        // Riga Configurazione Strategia (Paradigma)
        JPanel strategyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        strategyPanel.add(new JLabel("Seleziona Paradigma Concorrente: "));
        comboStrategy = new JComboBox<>(new String[]{"Virtual Threads", "RxJava (Reactive)", "Vert.x (Event Loop)"});
        strategyPanel.add(comboStrategy);

        // Riga Pulsanti Start/Stop
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 5));
        btnStop = new JButton("Stop");
        btnStop.setBackground(new Color(231, 76, 60)); btnStop.setForeground(Color.WHITE);
        btnStop.setFont(new Font("Arial", Font.BOLD, 14)); btnStop.setEnabled(false);

        btnStart = new JButton("Start");
        btnStart.setBackground(new Color(46, 204, 113)); btnStart.setForeground(Color.WHITE);
        btnStart.setFont(new Font("Arial", Font.BOLD, 14));

        buttonPanel.add(btnStop); buttonPanel.add(btnStart);

        controlsPanel.add(dirPanel);
        controlsPanel.add(strategyPanel);
        controlsPanel.add(buttonPanel);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 1.0; gbc.weighty = 0.4;
        gbc.fill = GridBagConstraints.BOTH; gbc.insets = new Insets(0, 0, 0, 0);
        add(controlsPanel, gbc);

        // --- Event Listeners ---
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedDirectory = fc.getSelectedFile();
                lblSelectedDir.setText(selectedDirectory.getAbsolutePath());
            }
        });

        btnStart.addActionListener(e -> startAnalysis());
        btnStop.addActionListener(e -> stopAnalysis());
    }

    private void startAnalysis() {
        if (selectedDirectory == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una directory!", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Risoluzione polimorfica della strategia scelta dall'utente
        String selected = (String) comboStrategy.getSelectedItem();
        switch (selected) {
            case "Virtual Threads" -> currentStrategy = new VTStrategy();
            case "RxJava (Reactive)" -> currentStrategy = new RxStrategy();
            case "Vert.x (Event Loop)" -> currentStrategy = new VertxStrategy();
        }

        long maxFS = 600 * 1024 * 1024L; // 600 MB
        int nb = 6;
        currentReport = new Report(maxFS, nb);

        setUiSearchingState(true);

        // Avvia la strategia in modo non bloccante fornendo la callback di fine
        currentStrategy.startReport(selectedDirectory.getAbsolutePath(), maxFS, nb, currentReport, () -> {
            SwingUtilities.invokeLater(this::finishAnalysis);
        });

        // Avvio polling di refresh grafico comune per tutti i paradigmi
        updateTimer = new Timer(true);
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> chartPanel.updateReport(currentReport));
            }
        }, 0, 100);
    }

    private void stopAnalysis() {
        if (currentStrategy != null) {
            currentStrategy.stopReport();
        }
        finishAnalysis();
    }

    private void finishAnalysis() {
        if (updateTimer != null) {
            updateTimer.cancel();
        }
        setUiSearchingState(false);
        chartPanel.updateReport(currentReport); // Render finale statico
    }

    private void setUiSearchingState(boolean isSearching) {
        btnStart.setEnabled(!isSearching);
        btnBrowse.setEnabled(!isSearching);
        comboStrategy.setEnabled(!isSearching);
        btnStop.setEnabled(isSearching);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FSStatMultiGUI().setVisible(true));
    }
}*/