package Server;

import Server.Logs.MySQLConnection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.sql.SQLException;

public class StopServer extends Thread {
    private final int port;
    private final Server s;
    private MySQLConnection sql;

    public StopServer(int port, Server s) {
        try {
            sql = new MySQLConnection();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | SQLException e) {
            e.printStackTrace();
        }
        if (port <= 0) {
            String portErr = "Closing port problem, check configuration file!";
            sql.writeServerLog(portErr);
            throw new IllegalArgumentException(portErr);

        }
        this.port = port;
        this.s = s;
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(this.port);
            serverSocket.accept().close();
            serverSocket.close();
            String stpp = "Stop signal received, shut down in progress.";
            if (sql != null && sql.isAlive()) sql.writeServerLog(stpp);
            s.stopServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
