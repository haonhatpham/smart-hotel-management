package com.pnh.utils;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailUtil {

    private static final Logger LOG = Logger.getLogger(MailUtil.class.getName());

    public static void sendMail(String to, String subject, String htmlContent) {
        final String from = "haopham081098@gmail.com"; // Gmail của bạn
        final String password = "jtna nttl wcqb ihig"; // App Password nếu dùng Gmail


        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });
        session.setDebug(false);  
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=UTF-8");

            Transport.send(message);
            LOG.fine("Email sent to: " + to);

        } catch (MessagingException e) {
            LOG.log(Level.WARNING, "Send mail failed: " + e.getMessage(), e);
        }
    }
}
