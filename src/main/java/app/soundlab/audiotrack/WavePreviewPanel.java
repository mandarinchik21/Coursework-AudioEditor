package app.soundlab.audiotrack;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class WavePreviewPanel extends JPanel {
    private final String fileName;

    public WavePreviewPanel(File audioFile) {
        this.fileName = audioFile == null ? "<none>" : audioFile.getName();
        System.out.println("AudioWaveformPanel: displaying simulated waveform for '" + fileName + "'");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GRAY);
        g.drawRect(5, 5, getWidth() - 10, getHeight() - 10);
        g.drawString("Waveform (simulated)", 20, 25);
        g.drawString("File: " + fileName, 20, 45);
        g.setColor(new Color(30, 144, 255));

        int mid = getHeight() / 2;
        int w = getWidth();

        for (int x = 10; x < w - 10; x++) {
            double v = Math.sin(x * 0.05) * (getHeight() * 0.3);
            int y = mid - (int) v;
            g.drawLine(x, y, x, mid);
        }
    }
}