package app.soundlab.audit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class OpenOperationLogger implements EventSink {
    private File logFile;

    public OpenOperationLogger(File logFile) {
       this.logFile = logFile;
    }

    @Override
    public void update() {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write("File was opened at " + LocalTime.now() + " " + LocalDate.now() + "\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
