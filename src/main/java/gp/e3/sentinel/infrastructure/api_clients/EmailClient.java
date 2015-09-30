package gp.e3.sentinel.infrastructure.api_clients;

import gp.e3.sentinel.domain.entities.Request;
import gp.e3.sentinel.domain.entities.User;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EmailClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailClient.class);

    public static final String HOST_NAME = "smtp.googlemail.com";
    public static final int SMTP_PORT = 465;
    public static final String SENDER_USERNAME = "e3.granpanda@gmail.com";
    public static final String SENDER_PASSWORD = "bsip5102";
    public static final String SENDER_EMAIL = "e3.granpanda@gmail.com";

    public static String NEW_LINE = java.lang.System.getProperty("line.separator");

    private String getMailBody(Request request) {

        String systemInfo = "ID: " + request.getSystemId() + NEW_LINE +
                "Name: " + request.getSystemName() + NEW_LINE +
                "URL: " + request.getSystemUrl();

        String requestInfo = "Http status code: " + request.getHttpResponseStatusCode() + NEW_LINE +
                "Http entity: " + request.getHttpResponseEntity() + NEW_LINE +
                "execution time: " + request.getRequestExecutionTimeInMilliseconds() + " milliseconds.";

        String thanks = "Thanks, " + NEW_LINE + NEW_LINE + "The E3 engineering team." + NEW_LINE;

        String mailBody = NEW_LINE + "The system: " + NEW_LINE + NEW_LINE + systemInfo + NEW_LINE + NEW_LINE + NEW_LINE +
                "Failed on the following request: " + NEW_LINE + NEW_LINE + requestInfo + NEW_LINE + NEW_LINE +
                thanks + NEW_LINE;

        return mailBody;
    }

    public boolean sendEmail(Request request, List<User> systemUsers) {

        boolean emailWasSent = false;

        try {

            Email mail = new SimpleEmail();
            mail.setHostName(HOST_NAME);
            mail.setSmtpPort(SMTP_PORT);

            String senderUsername = SENDER_USERNAME;
            String senderPassword = SENDER_PASSWORD;
            mail.setAuthenticator(new DefaultAuthenticator(senderUsername, senderPassword));
            mail.setSSLOnConnect(true);
            mail.setFrom(SENDER_EMAIL);

            String systemName = request.getSystemName();
            DateTime requestExecutionDate = request.getRequestExecutionDate();
            mail.setSubject("E3 Warning: " + systemName + " at " + requestExecutionDate.toString());
            String mailBody = getMailBody(request);
            mail.setMsg(mailBody);

            for (User user : systemUsers) {
                mail.addTo(user.getMail());
            }

            mail.send();
            emailWasSent = true;

            LOGGER.info("sendEmail: " + mailBody);

        } catch (EmailException e) {

            LOGGER.error("sendEmail", e);
        }

        return emailWasSent;
    }
}
