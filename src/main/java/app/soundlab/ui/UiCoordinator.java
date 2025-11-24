package app.soundlab.ui;

import app.soundlab.audit.AuditLog;
import app.soundlab.audiotrack.*;
import app.soundlab.audioencoder.AudioEncoder;
import app.soundlab.audioencoder.EncoderFactory;
import app.soundlab.service.AudioService;
import app.soundlab.service.SegmentService;
import app.soundlab.service.WorkspaceService;
import app.soundlab.editor.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

@SuppressWarnings({"unused"})
class UiCoordinator implements UiBus {
    private final JButton loadFileButton;
    private final JButton cutButton;
    private final JButton copyButton;
    private final JButton pasteButton;
    private final JButton deformButton;
    private final JTextField startField;
    private final JTextField endField;
    private final JTextField segmentLabelField;
    private final JButton applyButton;
    private final JLabel fileLabel;
    private final JPanel waveformPanel;
    private final AuditLog logger;
    private final JButton saveButton;
    private final JPanel lastActionsPanel;
    private final JScrollPane scrollPane;
    private final AudioService audioService = new AudioService();
    private final SegmentService segmentService = new SegmentService();
    private final WorkspaceService workspaceService = new WorkspaceService();

    private File selectedFile;
    private SegmentEntity currentTrack;

    public UiCoordinator(JButton loadFileButton, JLabel fileLabel, JButton cutButton, JButton copyButton, JButton pasteButton, JButton deformButton,
                         JTextField startField, JTextField endField, JTextField segmentLabelField, JButton applyButton,
                         JPanel waveformPanel, JButton saveButton, AuditLog logger,
                         JPanel lastActionsPanel, JScrollPane scrollPane) {
        this.loadFileButton = loadFileButton;
        this.fileLabel = fileLabel;
        this.cutButton = cutButton;
        this.copyButton = copyButton;
        this.pasteButton = pasteButton;
        this.deformButton = deformButton;
        this.startField = startField;
        this.endField = endField;
        this.segmentLabelField = segmentLabelField;
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
            case "loadFile" -> handleLoadFile();
            case "cut" -> handleCut();
            case "copy" -> handleCopy();
            case "paste" -> handlePaste();
            case "deform" -> handleMorph();
            case "apply" -> handleApplyBounds();
            case "save" -> handleSave();
            default -> throw new IllegalArgumentException("Unknown event: " + event);
        }
    }

    private void handleLoadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                long startTime = System.currentTimeMillis();
                System.out.println("[UI] Starting file load...");
                
                selectedFile = fileChooser.getSelectedFile();
                String filePath = selectedFile.getAbsolutePath();
                System.out.println("[UI] Selected file: " + filePath);
                
                String fileName = selectedFile.getName().toLowerCase();
                if (!fileName.endsWith(".mp3") && !fileName.endsWith(".ogg") && !fileName.endsWith(".flac")) {
                    JOptionPane.showMessageDialog(null, 
                        "This file format is not supported. Choose an MP3, OGG, or FLAC file.",
                        "Unsupported Format", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!audioService.exists(filePath)) {
                    String format = filePath.substring(filePath.lastIndexOf('.') + 1);
                    String title = selectedFile.getName();
                    audioService.createAudio(title, format, filePath);
                }

                long beforeResolve = System.currentTimeMillis();
                System.out.println("[UI] Resolving track (JAVE conversion + PCM load)...");
                currentTrack = resolveFor(selectedFile);
                long afterResolve = System.currentTimeMillis();
                System.out.println("[UI] Track resolved in " + (afterResolve - beforeResolve) + "ms");
                
                String title = selectedFile.getName();
                fileLabel.setText(title.length() > 20 ? title.substring(0, 20) + "..." : title);
                
                long beforeWaveform = System.currentTimeMillis();
                System.out.println("[UI] Rendering waveform from loaded track data...");
                renderWaveformFromTrack();
                long afterWaveform = System.currentTimeMillis();
                System.out.println("[UI] Waveform rendered in " + (afterWaveform - beforeWaveform) + "ms");
                
                logger.fileOpen();
                
                long totalTime = System.currentTimeMillis() - startTime;
                System.out.println("[UI] Total file load time: " + totalTime + "ms");
            } catch (IllegalArgumentException e) {
                logError("Load file", e);
                JOptionPane.showMessageDialog(null, 
                    "File format not supported: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                logError("Load file", e);
                JOptionPane.showMessageDialog(null, 
                    "Could not load the audio file:\n" + e.getMessage() + 
                    "\n\nEnsure ffmpeg is installed to support MP3/OGG/FLAC formats.",
                    "File Loading Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void handleCut() {
        if (currentTrack == null) {
            JOptionPane.showMessageDialog(null, "You need to load an audio file first.");
            return;
        }
        try {
            int start = Integer.parseInt(startField.getText());
            int end = Integer.parseInt(endField.getText());
            int trackDuration = currentTrack.getDurationSeconds();
            if (!isSegmentValid(start, end, trackDuration)) {
                JOptionPane.showMessageDialog(null, "Invalid segment range. Make sure 0 <= Start < End <= " + trackDuration);
                return;
            }
            int startSamples = currentTrack.getSamplesBySeconds(start);
            int endSamples = currentTrack.getSamplesBySeconds(end);
            
            SelectUseCase selectUseCase = new SelectUseCase(startSamples, endSamples);
            selectUseCase.edit(currentTrack);
            
            CutUseCase cutUseCase = new CutUseCase();
            cutUseCase.edit(currentTrack);
            
            renderWaveformFromTrack();
        } catch (NumberFormatException e) {
            logError("Cut segment - invalid number", e);
            JOptionPane.showMessageDialog(null, "Invalid input. Enter numeric values for Start and End fields.");
        } catch (Exception e) {
            logError("Cut segment", e);
            JOptionPane.showMessageDialog(null, "Failed to cut segment: " + e.getMessage());
        }
    }

    private void handleApplyBounds() {
        if (currentTrack == null || selectedFile == null) {
            JOptionPane.showMessageDialog(null, "You need to load an audio file first.");
            return;
        }
        try {
            int start = Integer.parseInt(startField.getText());
            int end = Integer.parseInt(endField.getText());
            int trackDuration = currentTrack.getDurationSeconds();
            if (trackDuration == 0) {
                JOptionPane.showMessageDialog(null, "Audio data is empty. Load a valid file.");
                return;
            }
            if (!isSegmentValid(start, end, trackDuration)) {
                JOptionPane.showMessageDialog(null,
                        String.format("Invalid segment range. Make sure 0 <= Start < End <= %d.", trackDuration));
                return;
            }
            int startSamples = currentTrack.getSamplesBySeconds(start);
            int endSamples = currentTrack.getSamplesBySeconds(end);
            
            SelectUseCase selectUseCase = new SelectUseCase(startSamples, endSamples);
            selectUseCase.edit(currentTrack);
            
            String labelInput = segmentLabelField.getText();
            String label = (labelInput == null || labelInput.isBlank()) ? null : labelInput.trim();
            
            segmentService.createSegment(selectedFile.getAbsolutePath(), start, end, label);
            segmentLabelField.setText("");
        } catch (NumberFormatException e) {
            logError("Apply bounds - invalid number", e);
                JOptionPane.showMessageDialog(null, "Invalid input. Enter numeric values for Start and End fields.");
        } catch (Exception e) {
            logError("Apply bounds", e);
            JOptionPane.showMessageDialog(null, "An unexpected error has occurred: " + e.getMessage());
        }
    }

    private void handleCopy() {
        if (currentTrack == null) {
            JOptionPane.showMessageDialog(null, "You need to load an audio file first.");
            return;
        }
        try {
            int start = Integer.parseInt(startField.getText());
            int end = Integer.parseInt(endField.getText());
            int trackDuration = currentTrack.getDurationSeconds();
            if (!isSegmentValid(start, end, trackDuration)) {
                JOptionPane.showMessageDialog(null, "Invalid segment range. Make sure 0 <= Start < End <= " + trackDuration + ".");
                return;
            }
            int startSamples = currentTrack.getSamplesBySeconds(start);
            int endSamples = currentTrack.getSamplesBySeconds(end);
            
            SelectUseCase selectUseCase = new SelectUseCase(startSamples, endSamples);
            selectUseCase.edit(currentTrack);
            
            CopyUseCase copyUseCase = new CopyUseCase();
            copyUseCase.edit(currentTrack);
        } catch (NumberFormatException e) {
            logError("Copy segment - invalid number", e);
            JOptionPane.showMessageDialog(null, "Invalid input. Enter numeric values for Start and End fields.");
        } catch (Exception e) {
            logError("Copy segment", e);
            JOptionPane.showMessageDialog(null, "Failed to copy segment: " + e.getMessage());
        }
    }

    private void handlePaste() {
        if (currentTrack == null) {
            JOptionPane.showMessageDialog(null, "You need to load an audio file first.");
            return;
        }
        try {
            String input = JOptionPane.showInputDialog("Enter the paste position in seconds:");
            if (input == null) {
                return;
            }
            int positionInSeconds = Integer.parseInt(input);
            
            PasteUseCase pasteUseCase = new PasteUseCase(positionInSeconds);
            pasteUseCase.edit(currentTrack);
            
            renderWaveformFromTrack();
        } catch (NumberFormatException e) {
            logError("Paste segment - invalid number", e);
            JOptionPane.showMessageDialog(null, "Invalid input. Enter a numeric value for the paste position.");
        } catch (Exception e) {
            logError("Paste segment", e);
            JOptionPane.showMessageDialog(null, "Failed to paste segment: " + e.getMessage());
        }
    }

    private void handleMorph() {
        if (currentTrack == null) {
            JOptionPane.showMessageDialog(null, "You need to load an audio file first.");
            return;
        }
        try {
            String factorInput = JOptionPane.showInputDialog("Enter the deformation factor (for example, 0.5 or 2.0):");
            if (factorInput == null) {
                return;
            }
            double factor = Double.parseDouble(factorInput);
            int start = Integer.parseInt(startField.getText());
            int end = Integer.parseInt(endField.getText());
            int startSamples = currentTrack.getSamplesBySeconds(start);
            int endSamples = currentTrack.getSamplesBySeconds(end);
            
            SelectUseCase selectUseCase = new SelectUseCase(startSamples, endSamples);
            selectUseCase.edit(currentTrack);
            
            MorphUseCase morphUseCase = new MorphUseCase(factor);
            morphUseCase.edit(currentTrack);
            
            renderWaveformFromTrack();
        } catch (NumberFormatException e) {
            logError("Morph segment - invalid number", e);
            JOptionPane.showMessageDialog(null, "Invalid input. Enter numeric values for Start and End fields.");
        } catch (Exception e) {
            logError("Morph segment", e);
            JOptionPane.showMessageDialog(null, "Failed to deform segment: " + e.getMessage());
        }
    }

    private void renderWaveform(File audioFile) {
        waveformPanel.removeAll();
        waveformPanel.setLayout(new BorderLayout());
        waveformPanel.add(new WavePreviewPanel(audioFile), BorderLayout.CENTER);
        waveformPanel.setVisible(true);
        waveformPanel.revalidate();
        waveformPanel.repaint();
    }

    private void renderWaveformFromTrack() {
        if (currentTrack == null) {
            return;
        }
        try {
            waveformPanel.removeAll();
            waveformPanel.setLayout(new BorderLayout());
            JPanel previewPanel = segmentService.outputWaveForm(currentTrack);
            waveformPanel.add(previewPanel, BorderLayout.CENTER);
            waveformPanel.setVisible(true);
            waveformPanel.revalidate();
            waveformPanel.repaint();
        } catch (Exception e) {
            logError("Render waveform", e);
            JOptionPane.showMessageDialog(null, "Failed to render waveform: " + e.getMessage());
        }
    }

    private SegmentEntity resolveFor(File file) {
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

    private void handleSave() {
        if (currentTrack == null || selectedFile == null) {
            JOptionPane.showMessageDialog(null, "You need to load an audio file first.");
            return;
        }
        
        String[] formats = {"MP3", "OGG", "FLAC"};
            String selectedFormat = (String) JOptionPane.showInputDialog(
            null,
            "Choose the output format:",
            "Save As",
            JOptionPane.QUESTION_MESSAGE,
            null,
            formats,
            formats[0]
        );
        
        if (selectedFormat == null) {
            return;
        }
        
        String formatLower = selectedFormat.toLowerCase();
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save As " + selectedFormat);
        int returnValue = fileChooser.showSaveDialog(null);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File saveFile = fileChooser.getSelectedFile();
            String filePath = saveFile.getAbsolutePath();
            
            if (!filePath.toLowerCase().endsWith("." + formatLower)) {
                filePath += "." + formatLower;
            }
            
            try {
                System.out.println("[UI] Generating WAV data in memory...");
                byte[] wavData = currentTrack.generateWavData();
                System.out.println("[UI] WAV data generated, size: " + wavData.length + " bytes");
                
                File tempWavFile = File.createTempFile("audio_editor_temp_", ".wav");
                tempWavFile.deleteOnExit();
                System.out.println("[UI] Writing WAV data to temp file: " + tempWavFile.getAbsolutePath());
                
                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(tempWavFile)) {
                    fos.write(wavData);
                    fos.flush();
                    fos.getFD().sync();
                }
                
                System.out.println("[UI] Temp WAV file created, size: " + tempWavFile.length() + " bytes");
                
                File outputFile = new File(filePath);
                System.out.println("[UI] Converting WAV to " + selectedFormat + "...");
                convertWavToFormat(tempWavFile, outputFile, formatLower);
                System.out.println("[UI] Conversion completed successfully");
                
                if (!tempWavFile.delete()) {
                    tempWavFile.deleteOnExit();
                }
                
                String workspaceTitle = outputFile.getName();
                int workspaceId = workspaceService.createWorkspace(workspaceTitle);
                workspaceService.addAudioToWorkspace(workspaceId, selectedFile.getAbsolutePath());
                
                JOptionPane.showMessageDialog(null, 
                    "File has been saved successfully as " + selectedFormat + "!\n" + outputFile.getName());
            } catch (Exception e) {
                logError("Save file", e);
                JOptionPane.showMessageDialog(null, "Failed to save file: " + e.getMessage());
            }
        }
    }
    
    private void convertWavToFormat(File wavFile, File outputFile, String format) throws Exception {
        if (!wavFile.exists()) {
            throw new Exception("Input WAV file does not exist: " + wavFile.getAbsolutePath());
        }
        if (wavFile.length() == 0) {
            throw new Exception("Input WAV file is empty: " + wavFile.getAbsolutePath());
        }
        System.out.println("[UI] Converting WAV file: " + wavFile.getAbsolutePath() + " (size: " + wavFile.length() + " bytes)");
        
        AudioEncoder<File> encoder = EncoderFactory.getEncoder(format);
        encoder.encode(wavFile, outputFile);
    }

    private boolean isSegmentValid(int start, int end, int max) {
        return start >= 0 && end <= max && start < end;
    }

    private void logError(String context, Exception e) {
        System.err.println("[UI] " + context + " failed: " + e.getMessage());
        e.printStackTrace();
    }
}

