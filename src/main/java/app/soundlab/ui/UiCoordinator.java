package app.soundlab.ui;

import app.soundlab.audiotrack.*;
import app.soundlab.audit.AuditLog;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@SuppressWarnings({"unused"})
class UiCoordinator implements UiBus {
    private final JButton loadFileButton;
    private final JButton convertToMp3Button;
    private final JButton convertToOggButton;
    private final JButton convertToFlacButton;
    private final JButton cutButton;
    private final JButton copyButton;
    private final JButton pasteButton;
    private final JButton deformButton;
    private final JTextField startField;
    private final JTextField endField;
    private final JButton applyButton;
    private final JLabel fileLabel;
    private final JPanel waveformPanel;
    private final AuditLog logger;
    private File selectedFile;
    private SegmentEntity currentTrack;
    private final JButton saveButton;
    private final JPanel lastActionsPanel;
    private final JScrollPane scrollPane;

    public UiCoordinator(JButton loadFileButton, JButton convertToMp3Button, JButton convertToOggButton, JButton convertToFlacButton,
                               JLabel fileLabel, JButton cutButton, JButton copyButton, JButton pasteButton, JButton deformButton,
                               JTextField startField, JTextField endField, JButton applyButton, JPanel waveformPanel, JButton saveButton, AuditLog logger,
                               JPanel lastActionsPanel, JScrollPane scrollPane) {
        this.loadFileButton = loadFileButton;
        this.convertToMp3Button = convertToMp3Button;
        this.convertToOggButton = convertToOggButton;
        this.convertToFlacButton = convertToFlacButton;
        this.fileLabel = fileLabel;
        this.cutButton = cutButton;
        this.copyButton = copyButton;
        this.pasteButton = pasteButton;
        this.deformButton = deformButton;
        this.startField = startField;
        this.endField = endField;
        this.applyButton = applyButton;
        this.waveformPanel = waveformPanel;
        this.saveButton = saveButton;
        this.logger = logger;
        this.lastActionsPanel = lastActionsPanel;
        this.scrollPane = scrollPane;
        wireEvents();
    }

    private void wireEvents() {
        loadFileButton.addActionListener(e -> notify(loadFileButton, "loadFile"));
        convertToMp3Button.addActionListener(e -> notify(convertToMp3Button, "convertToMp3"));
        convertToOggButton.addActionListener(e -> notify(convertToOggButton, "convertToOgg"));
        convertToFlacButton.addActionListener(e -> notify(convertToFlacButton, "convertToFlac"));
        cutButton.addActionListener(e -> notify(cutButton, "cut"));
        copyButton.addActionListener(e -> notify(copyButton, "copy"));
        pasteButton.addActionListener(e -> notify(pasteButton, "paste"));
        deformButton.addActionListener(e -> notify(deformButton, "deform"));
        applyButton.addActionListener(e -> notify(applyButton, "apply"));
        saveButton.addActionListener(e -> notify(saveButton, "save"));
    }

    @Override
    public void notify(Component sender, String event) {
        switch (event) {
            case "loadFile":
                System.out.println("[UI] Load File clicked (simulated)");
                break;

            case "convertToMp3":
                System.out.println("[UI] Convert to MP3 clicked (simulated)");
                break;

            case "convertToOgg":
                System.out.println("[UI] Convert to OGG clicked (simulated)");
                break;

            case "convertToFlac":
                System.out.println("[UI] Convert to FLAC clicked (simulated)");
                break;

            case "cut":
                System.out.println("[UI] Cut clicked (simulated)");
                break;

            case "copy":
                System.out.println("[UI] Copy clicked (simulated)");
                break;

            case "paste":
                System.out.println("[UI] Paste clicked (simulated)");
                break;

            case "deform":
                System.out.println("[UI] Deform clicked (simulated)");
                break;
            case "apply":
                System.out.println("[UI] Apply segment bounds clicked (simulated)");
                break;
            case "save":
                System.out.println("[UI] Save clicked (simulated)");
                break;
            default:
                throw new IllegalArgumentException("Unknown event: " + event);
        }
    }

    private void handleLoadFile() {}
    private void handleConvert(String format) {}
    private void handleCut() {}
    private void handleApplyBounds() {}
    private void handleCopy() {}
    private void handlePaste() {}
    private void handleMorph() {}
    private void renderWaveform(File audioFile) {}

    private SegmentEncodingAdapter buildAdapterFor(File file) {
        SegmentEntity audiotrack = resolveTrackFor(file);
        return new SegmentEncodingAdapter(audiotrack);
    }

    private SegmentEntity resolveTrackFor(File file) {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".mp3")) {
            return new Mp3Asset(file.getAbsolutePath());
        } else if (fileName.endsWith(".ogg")) {
            return new OggAsset(file.getAbsolutePath());
        } else if (fileName.endsWith(".flac")) {
            return new FlacAsset(file.getAbsolutePath());
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileName);
        }
    }

    private void handleSave() {}
}

