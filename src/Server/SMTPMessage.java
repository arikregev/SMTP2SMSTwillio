package Server;

import java.util.Objects;

public class SMTPMessage {
    private String from;
    private String to;
    private String subject;
    private String message;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public static String getConRes() {
        return "220 sms.ofekcloud.com Ready to receive mail -=- ESMTP\n";
    }

    public static String ansHELO() {
        return "250 sms.ofekcloud.com\n";
    }

    public static String sendOK() {
        return "250 OK!\n";
    }

    public static String sendOKMessageQueued() {
        return "250 OK! Message queued.\n";
    }

    public static String startSendingMessage() {
        return "354 Start sending the message\n";
    }

    public boolean isMessageValid(){
        return from != null && to != null && subject != null;
    }
    @Override
    public String toString() {
        return "SMTPMessage{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", subject='" + (subject).substring(0,subject.length()-2) + "XX" + '\'' +
                '}';
    }
}
