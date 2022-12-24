package Server.Logs;

import Server.ServerThread;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLogger {
    private ServerThread s;
    private Path logsPath;

    public ServerLogger(String serverLogs){
        Path p = Paths.get(serverLogs);
        this.logsPath = Paths.get(p.toAbsolutePath() + this.osPathDirection()
                + "Server-" + getDate() + ".log");
        if (!Files.exists(this.logsPath)) {
            try {
                Files.createFile(this.logsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String osPathDirection(){
        return System.getProperty("os.name").toLowerCase().contains("linux") ? "/" : "\\";
    }
    public ServerLogger(ServerThread s, String logsPath) {
        this.s = s;
        this.logsPath = Paths.get(logsPath);

        if (!Files.exists(this.logsPath)) {
            try {
                Files.createDirectories(this.logsPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getDateTime() {
        return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss").format(LocalDateTime.now());
    }

    public String getDate() {
        return DateTimeFormatter.ofPattern("dd.MM.yyyy").format(LocalDateTime.now());
    }

    public void log(String s) {
        try {
            Files.write(this.logsPath, (this.getDateTime() + ": " + s + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void logSuccessfulProcess() {
        Path file = Paths.get(logsPath.toAbsolutePath() + this.osPathDirection()
                + this.s.getDest() + ".log");
        try {
            if(!Files.exists(file))
                Files.createFile(file);
            Files.write(file,(this.getDateTime() + ": " + s.toString()  + "\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
