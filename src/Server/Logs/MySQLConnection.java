package Server.Logs;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLConnection {
    Connection conn;
    String url;
    String dbName;
    final String SMSLogsTableName = "CodesLogs";
    final String ServerLogs = "ServerLogs";

    public MySQLConnection() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        url = "jdbc:mysql://127.0.0.1:3306/";
        dbName = "Twilio";
        String userName = "twilio";
        String password = "l0k@Lh0$t";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver).getDeclaredConstructor().newInstance();
        conn = DriverManager.getConnection(url + dbName, userName, password);
    }

    public boolean isAlive() { return this.conn != null; }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException throwables) {}
        }
    }

    public void writeCodeLog(String emailFrom, String numTo, String subject, String serverResponse) {
        try {
            String subjectEdited = subject.substring(0,subject.length()-2).concat("XX");
            this.conn.createStatement().execute("INSERT INTO " + this.SMSLogsTableName +
                    "(DateNTime,EmailFrom,SMSTo,Subject,ServerResponse) VALUES ((SELECT NOW()),'" +
                        emailFrom + " ','" + numTo + " ','" + subjectEdited + " ','" + serverResponse+" ');");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getCause());
        }

    }
    public void updateStatistics(){
        ResultSet rsCodes;
        try {
            rsCodes = this.conn.createStatement().executeQuery("select SMSTo, COUNT(*) from CodesLogs group by SMSTo;");
            while(rsCodes.next()){
                int count = rsCodes.getInt("COUNT(*)");
                String id = rsCodes.getString("SMSTo");
                this.conn.createStatement().execute(
                        "INSERT INTO Statistics (Num_id,NumOfUsages,LastUpdated) VALUES('" + id + "','"+ count +
                                "',(SELECT NOW())) ON DUPLICATE KEY UPDATE NumOfUsages = '"+count+"', LastUpdated = (SELECT NOW())");

            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void writeServerLog(String message){
        try {
            this.conn.createStatement().execute("INSERT INTO " + this.ServerLogs +
                    "(DateNTime,Message) VALUES ((SELECT NOW()),'" + message +"');");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println(throwables.getCause());
        }
    }
}
