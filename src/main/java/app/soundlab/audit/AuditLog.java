package app.soundlab.audit;

import java.io.File;

public class AuditLog {
    EventsHub eventManager;
    final File logFile = new File("audit/logs.txt");
    OpenOperationLogger fileOpenLog;

    public AuditLog() {
        eventManager = new EventsHub("openFile");

        fileOpenLog = new OpenOperationLogger(logFile);
        eventManager.subscribe(fileOpenLog, "openFile");
    }

    public void fileOpen() {
        eventManager.notifySubscribers("openFile");
    }
}
