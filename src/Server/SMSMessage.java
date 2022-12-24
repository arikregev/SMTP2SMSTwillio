package Server;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class SMSMessage implements Callable<String> {
    private final SMTPMessage smtpMessage;
    private final String senderName;
    private final String ACCOUNT_SID;
    private final String AUTH_TOKEN;

    public SMSMessage(SMTPMessage smtpMessage, ConcurrentHashMap<String, String> conf) {
        this.smtpMessage = smtpMessage;
        this.senderName = conf.getOrDefault("SmsSenderName", null);
        this.ACCOUNT_SID = conf.getOrDefault("ACCOUNT_SID", null);
        this.AUTH_TOKEN = conf.getOrDefault("AUTH_TOKEN",null);
    }

    @Override
    public String call() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        return (Message.creator(
                new com.twilio.type.PhoneNumber("+" + smtpMessage.getTo()),
                new com.twilio.type.PhoneNumber("+" + smtpMessage.getFrom()),
                smtpMessage.getSubject()).setFrom(this.senderName).create()).getSid();
    }
}
