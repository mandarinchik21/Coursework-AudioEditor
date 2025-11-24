package app.soundlab.ui;

import app.soundlab.audit.AuditLog;
import app.soundlab.db.DatabaseBootstrap;
import app.soundlab.service.RecentItemsService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow {
    private static final Color BACKGROUND = new Color(30, 30, 30);
    private static final Color PANEL = new Color(40, 40, 40);
    private static final Color BORDER = new Color(60, 60, 60);
    private static final Color TEXT = new Color(220, 220, 220);
    private static final Color BUTTON = new Color(50, 50, 50);
    private static final Color TOOLBAR = new Color(35, 35, 35);

    public static void main(String[] args) {
        DatabaseBootstrap.init();

        JFrame frame = new JFrame("Audio Editor");
        frame.setSize(800, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8, 8));
        frame.getContentPane().setBackground(BACKGROUND);

        JButton loadFileButton = createButton("Open Audio...", BUTTON, TEXT);
        JLabel fileLabel = createLabel("No file selected", TEXT);

        JPanel waveformPanel = new JPanel();
        waveformPanel.setVisible(false);
        waveformPanel.setPreferredSize(new Dimension(600, 160));
        waveformPanel.setBackground(PANEL);
        JButton toggleWaveformButton = createButton("Show Waveform", BUTTON, TEXT);
        toggleWaveformButton.addActionListener(e -> {
            boolean isVisible = waveformPanel.isVisible();
            waveformPanel.setVisible(!isVisible);
            toggleWaveformButton.setText(isVisible ? "Show Waveform" : "Hide Waveform");
        });
        waveformPanel.setBorder(BorderFactory.createLineBorder(BORDER));

        JButton cutButton = createButton("Cut", BUTTON, TEXT);
        JButton copyButton = createButton("Copy", BUTTON, TEXT);
        JButton pasteButton = createButton("Paste", BUTTON, TEXT);
        JButton deformButton = createButton("Morph", BUTTON, TEXT);

        JTextField startField = createTextField("Start time (s)", PANEL, TEXT, BORDER);
        JTextField endField = createTextField("End time (s)", PANEL, TEXT, BORDER);
        JTextField segmentLabelField = createTextField("Optional label (e.g., Chorus)", PANEL, TEXT, BORDER);

        JLabel startLabel = createLabel("Start (s):", TEXT);
        JLabel endLabel = createLabel("End (s):", TEXT);
        JLabel segmentLabel = createLabel("Label:", TEXT);

        JButton applyButton = createButton("Apply Bounds", BUTTON, TEXT);
        JButton saveButton = createButton("Save As...", BUTTON, TEXT);

        AuditLog logger = new AuditLog();
        JPanel lastActionsPanel = new JPanel();
        lastActionsPanel.setLayout(new BoxLayout(lastActionsPanel, BoxLayout.Y_AXIS));
        lastActionsPanel.setBackground(PANEL);

        JScrollPane scrollPane = new JScrollPane(lastActionsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER), "Recent Items",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Dialog", Font.PLAIN, 12), TEXT));
        scrollPane.setBackground(PANEL);
        scrollPane.getViewport().setBackground(PANEL);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // custom scrollbar appearance         
        Color scrollbarTrack = new Color(30, 30, 30);
        Color scrollbarThumb = new Color(70, 70, 70);
        
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setBackground(scrollbarTrack);
        verticalScrollBar.setForeground(scrollbarThumb);
        verticalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = scrollbarThumb;
                this.thumbDarkShadowColor = scrollbarThumb;
                this.thumbHighlightColor = scrollbarThumb;
                this.thumbLightShadowColor = scrollbarThumb;
                this.trackColor = scrollbarTrack;
                this.trackHighlightColor = scrollbarTrack;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setBackground(scrollbarTrack);
                return button;
            }
        });
        
        JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setBackground(scrollbarTrack);
        horizontalScrollBar.setForeground(scrollbarThumb);
        horizontalScrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = scrollbarThumb;
                this.thumbDarkShadowColor = scrollbarThumb;
                this.thumbHighlightColor = scrollbarThumb;
                this.thumbLightShadowColor = scrollbarThumb;
                this.trackColor = scrollbarTrack;
                this.trackHighlightColor = scrollbarTrack;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                button.setBackground(scrollbarTrack);
                return button;
            }
        });

        RecentItemsService audioService = new RecentItemsService();
        List<String> lastAudios = audioService.getLastAudios(3);
        List<String> lastSegments = audioService.getLastSegments(3);
        List<String> lastWorkspaces = audioService.getLastWorkspaces(3);

        JLabel lastAudiosLabel = createLabel("Last audios:", TEXT);
        lastActionsPanel.add(lastAudiosLabel);
        for (String audio : lastAudios) {
            lastActionsPanel.add(createLabel(audio, TEXT));
        }

        lastActionsPanel.add(Box.createVerticalStrut(10));

        JLabel segmentsLabel = createLabel("Edited segments:", TEXT);
        lastActionsPanel.add(segmentsLabel);
        for (String segment : lastSegments) {
            lastActionsPanel.add(createLabel(segment, TEXT));
        }

        lastActionsPanel.add(Box.createVerticalStrut(10));

        JLabel workspacesLabel = createLabel("Recent workspaces:", TEXT);
        lastActionsPanel.add(workspacesLabel);
        for (String workspace : lastWorkspaces) {
            lastActionsPanel.add(createLabel(workspace, TEXT));
        }

        lastActionsPanel.revalidate();
        lastActionsPanel.repaint();

        JToolBar topBar = new JToolBar();
        topBar.setFloatable(false);
        topBar.setBackground(TOOLBAR);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        topBar.add(loadFileButton);
        topBar.add(Box.createHorizontalGlue());
        topBar.add(toggleWaveformButton);

        JPanel leftPanel = createEditPanel(cutButton, copyButton, pasteButton, deformButton, saveButton);
        JPanel boundsPanel = createBoundsPanel(fileLabel, startLabel, endLabel, segmentLabel, 
            startField, endField, segmentLabelField, applyButton);
        JPanel centerPanel = createCenterPanel(boundsPanel, waveformPanel);
        JSplitPane mainSplitPane = createMainSplitPane(centerPanel, scrollPane);

        new UiCoordinator(
                loadFileButton, fileLabel, cutButton, copyButton, pasteButton, deformButton, 
                startField, endField, segmentLabelField, applyButton,
                waveformPanel, saveButton, logger, lastActionsPanel, scrollPane);

        UiGroup mainContainer = new UiGroup();
        UiGroup buttonPanel = new UiGroup();
        buttonPanel.add(new UiLeaf(loadFileButton));
        buttonPanel.add(new UiLeaf(cutButton));
        buttonPanel.add(new UiLeaf(copyButton));
        buttonPanel.add(new UiLeaf(pasteButton));
        buttonPanel.add(new UiLeaf(deformButton));
        buttonPanel.add(new UiLeaf(saveButton));
        mainContainer.add(buttonPanel);
        mainContainer.add(new UiLeaf(fileLabel));
        mainContainer.add(new UiLeaf(startField));
        mainContainer.add(new UiLeaf(endField));
        mainContainer.add(new UiLeaf(segmentLabelField));
        mainContainer.add(new UiLeaf(startLabel));
        mainContainer.add(new UiLeaf(endLabel));
        mainContainer.add(new UiLeaf(segmentLabel));
        mainContainer.add(new UiLeaf(waveformPanel));
        mainContainer.add(new UiLeaf(lastActionsPanel));
        mainContainer.operate();

        frame.add(topBar, BorderLayout.NORTH);
        frame.add(leftPanel, BorderLayout.WEST);
        frame.add(mainSplitPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private static JPanel createEditPanel(JButton cutButton, JButton copyButton, JButton pasteButton, 
                                         JButton deformButton, JButton saveButton) {
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(PANEL);
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(BORDER), "Edit",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new Font("Dialog", Font.PLAIN, 12), TEXT));

        JButton[] buttons = {cutButton, copyButton, pasteButton, deformButton, saveButton};
        for (JButton button : buttons) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
            leftPanel.add(button);
            leftPanel.add(Box.createVerticalStrut(8));
        }
        leftPanel.add(Box.createVerticalGlue());
        return leftPanel;
    }

    private static JPanel createBoundsPanel(JLabel fileLabel, JLabel startLabel, JLabel endLabel, 
                                          JLabel segmentLabel, JTextField startField, JTextField endField,
                                          JTextField segmentLabelField, JButton applyButton) {
        JPanel boundsPanel = new JPanel(new GridBagLayout());
        boundsPanel.setBackground(PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        boundsPanel.add(createLabel("File:", TEXT), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        boundsPanel.add(fileLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        boundsPanel.add(startLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        boundsPanel.add(startField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        boundsPanel.add(endLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        boundsPanel.add(endField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        boundsPanel.add(segmentLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        boundsPanel.add(segmentLabelField, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        boundsPanel.add(applyButton, gbc);

        return boundsPanel;
    }

    private static JPanel createCenterPanel(JPanel boundsPanel, JPanel waveformPanel) {
        JPanel centerPanel = new JPanel(new BorderLayout(8, 8));
        centerPanel.setBackground(BACKGROUND);
        centerPanel.add(boundsPanel, BorderLayout.NORTH);
        centerPanel.add(waveformPanel, BorderLayout.CENTER);
        return centerPanel;
    }

    private static JSplitPane createMainSplitPane(JPanel centerPanel, JScrollPane scrollPane) {
        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setTopComponent(centerPanel);
        mainSplitPane.setBottomComponent(scrollPane);
        mainSplitPane.setResizeWeight(0.7);
        mainSplitPane.setDividerLocation(490);
        mainSplitPane.setBorder(BorderFactory.createEmptyBorder());
        return mainSplitPane;
    }

    private static JButton createButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(bg.getRed() + 20, bg.getGreen() + 20, bg.getBlue() + 20)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private static JLabel createLabel(String text, Color fg) {
        JLabel label = new JLabel(text);
        label.setForeground(fg);
        return label;
    }

    private static JTextField createTextField(String tooltip, Color bg, Color fg, Color border) {
        JTextField field = new JTextField();
        field.setToolTipText(tooltip);
        field.setBackground(bg);
        field.setForeground(fg);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(border),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        field.setCaretColor(fg);
        return field;
    }
}