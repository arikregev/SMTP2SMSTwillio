package Server;

import Server.Logs.MySQLConnection;
import Server.Logs.ServerLogger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ServerThread extends Thread {
    private final Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private final SMTPMessage smtpMessage;
    private SMSMessage smsMessage;
    private String smsServerResponse; //TODO - log response
    private final ConcurrentHashMap<String, String> conf;
    private final ServerLogger sl;
    private MySQLConnection sql = null;

    public ServerThread(Socket cs, ConcurrentHashMap<String, String> conf, ServerLogger sl) {
        this.clientSocket = cs;
        this.conf = conf;
        this.smtpMessage = new SMTPMessage();
        this.sl = sl;
        try {
            this.sql = new MySQLConnection();
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.write(SMTPMessage.getConRes());
            out.flush();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.toLowerCase().contains("helo") || line.toLowerCase().contains("ehlo")) {
                    out.write(SMTPMessage.ansHELO());
                    out.flush();
                } else if (line.toLowerCase().contains("mail from")) {
                    String[] x = line.split(":");
                    smtpMessage.setFrom(x[1].replaceAll("<|>", ""));
                    out.write(SMTPMessage.sendOK());
                    out.flush();
                } else if (line.toLowerCase().contains("rcpt to")) {
                    String[] x = line.split(":");
                    x = (x[1].replaceAll("<|>", "").split("@"));
                    smtpMessage.setTo(x[0]);
                    out.write(SMTPMessage.sendOK());
                    out.flush();
                } else if (line.toLowerCase().contains("data")) {
                    out.write(SMTPMessage.startSendingMessage());
                    out.flush();
                } else if (line.toLowerCase().contains("subject")) {
                    String[] x = line.split(":");
                    smtpMessage.setSubject(x[1] + ": " + x[2]);
                } else if (line.toLowerCase().equals(".")) {
                    out.write(SMTPMessage.sendOKMessageQueued());
                    out.flush();
                    break;
                } else if (line.toLowerCase().contains("quit")) {
                    out.write("221 2.0.0 Bye");
                    out.flush();
                    break;
                } else
                    smtpMessage.setMessage(smtpMessage.getMessage() + line + "\n");
            }
            if (smtpMessage.isMessageValid()) {
                smsMessage = new SMSMessage(smtpMessage, this.conf);
                try {
                    this.smsServerResponse = smsMessage.call();
                    String result = this.smtpMessage.getTo() + ": Token was sent to the user. Twilio Response: " + this.getSmsServerResponse();
                    sl.log(result);
                    sql.writeServerLog(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    String exception = "Exception thrown: " + e.toString() + "\t" + this.getSMTPmessagecontent();
                    sl.log(exception);
                    sql.writeServerLog(exception);
                }
                (new ServerLogger(this, conf.getOrDefault("Path2Logs", null))).logSuccessfulProcess();
                sql.writeCodeLog(smtpMessage.getFrom(), smtpMessage.getTo(), smtpMessage.getSubject(), this.smsServerResponse);
            } else {
                String nonValid = "A non valid SMTP message received from: " + this.getIP() + "\t" + this.getSMTPmessagecontent();
                sl.log(nonValid);
                //sql.writeServerLog(nonValid); Writen only to file not SQL

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.stopThread();
        }
    }

    public void stopThread() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket.isConnected()) clientSocket.close();
            if (sql != null && sql.isAlive()) sql.closeConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSmsServerResponse() {
        return smsServerResponse;
    }

    public String getDest() {
        return this.smtpMessage.getTo();
    }

    @Override
    public String toString() {
        return smtpMessage.toString() + "\t" +
                "Response from SMS Server: " + smsServerResponse;
    }

    public String getIP() {
        return this.clientSocket.getRemoteSocketAddress().toString().replaceAll("/", "");
    }

    public String getSMTPmessagecontent() {
        return "{From: " + smtpMessage.getFrom() + " \t" +
                "To: " + smtpMessage.getTo() + " \t" +
                "Subject: " + smtpMessage.getSubject() + " }";
    }
}
