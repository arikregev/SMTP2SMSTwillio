package Server;

import Server.Logs.MySQLConnection;
import Server.Logs.ServerLogger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {
    private ServerSocket serverSocket;
    private final int port;
    private volatile boolean stop;
    private final List<ServerThread> stList;
    private ConcurrentHashMap<String, String> conf;
    private final ServerLogger sl;
    private MySQLConnection sql;

    public Server(ConcurrentHashMap<String, String> conf) {
        serverSocket = null;
        stop = false;
        this.conf = conf;
        this.port = Integer.parseInt(conf.getOrDefault("Port", "0"));
        if (this.port == 0)
            throw new IllegalArgumentException("Server listening port setting problem, Check config file!");
        this.stList = new LinkedList<ServerThread>();
        this.sl = new ServerLogger(conf.get("Path2Logs"));
        try {
            this.sql = new MySQLConnection();
        } catch (ClassNotFoundException | SQLException | InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            System.out.println("SQL Error!: " + e.toString());
            sl.log("SQL Error!: " + e.toString());
            this.stopServer();
        }
    }

    public void stopServer() {
        this.stop = true;
        String message = "Received kill signal, termination in progress...";
        if (sql != null && sql.isAlive()) this.sql.writeServerLog(message);
        this.sl.log(message);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stList.forEach(x -> {
            if (x.isAlive() && !x.isInterrupted())
                x.stopThread();
        });
        if (this.serverSocket.isBound())
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                String err = "Exception thrown: " + e.getMessage();
                sl.log(err);
                if (sql != null && sql.isAlive()) sql.writeServerLog(err);
                e.printStackTrace();
            }
        if(sql != null && sql.isAlive()) sql.closeConnection();
    }

    public void updateStatistics(){
        new Thread(()->{
            MySQLConnection sql = null;
            try {
                sql = new MySQLConnection();
                sql.updateStatistics();
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                    InvocationTargetException | InstantiationException | SQLException e) {
                e.printStackTrace();
            }finally{
                if(sql != null) sql.closeConnection();
            }
        }).start();

    }
    @Override
    public void run() {
        try {
            this.serverSocket = new ServerSocket(port);
            String message = "Server is ready to receive SMTP on port " + port;
            sl.log(message);
            sql.writeServerLog(message);
            while (!stop) {
                ServerThread serverThread = new ServerThread(serverSocket.accept(), conf, sl);
                String connected = "Connected to Server: " + serverThread.getIP() + " receiving in progress...";
                sl.log(connected);
                sql.writeServerLog(connected);
                stList.add(serverThread);
                serverThread.start();
                stList.removeIf(x -> !x.isAlive());
                this.updateStatistics();
            }
        } catch (IOException e) {
            if (this.stop) { this.stopServer(); } else {
                String exception = "Exception thrown: " + e.getMessage();
                sl.log(exception);
                sql.writeServerLog(exception);
                this.stopServer();
                e.printStackTrace();
            }
        }
    }
}