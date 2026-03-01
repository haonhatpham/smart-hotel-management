package com.pnh.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MailUtil {

    private static final Logger LOG = Logger.getLogger(MailUtil.class.getName());
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String BREVO_ENDPOINT = "https://api.brevo.com/v3/smtp/email";
    private static final ExecutorService MAIL_EXECUTOR = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "mail-sender");
        t.setDaemon(true);
        return t;
    });

    public static void sendMailAsync(String to, String subject, String htmlContent) {
        MAIL_EXECUTOR.submit(() -> sendMail(to, subject, htmlContent));
    }

    public static void sendMail(String to, String subject, String htmlContent) {
        String brevoApiKey = cfg("brevo.api.key", "BREVO_API_KEY");
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            sendViaBrevo(to, subject, htmlContent, brevoApiKey.trim());
            return;
        }

        // Fallback SMTP khi chưa cấu hình BREVO (không block callback vì đã gọi async từ controller)
        sendViaSmtp(to, subject, htmlContent);
    }

    private static void sendViaBrevo(String to, String subject, String htmlContent, String apiKey) {
        try {
            String fromEmail = cfg("mail.from", "MAIL_FROM");
            if (fromEmail == null || fromEmail.isBlank()) {
                LOG.warning("BREVO enabled but MAIL_FROM is missing.");
                return;
            }
            String fromName = cfg("mail.from.name", "MAIL_FROM_NAME");
            if (fromName == null || fromName.isBlank()) fromName = "Smart Hotel";

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", Map.of("name", fromName, "email", fromEmail));
            payload.put("to", new Object[]{Map.of("email", to)});
            payload.put("subject", subject);
            payload.put("htmlContent", htmlContent);
            byte[] body = MAPPER.writeValueAsBytes(payload);

            HttpURLConnection conn = (HttpURLConnection) new URL(BREVO_ENDPOINT).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(10000);
            conn.setDoOutput(true);
            conn.setRequestProperty("accept", "application/json");
            conn.setRequestProperty("content-type", "application/json");
            conn.setRequestProperty("api-key", apiKey);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(body);
            }

            int code = conn.getResponseCode();
            if (code >= 200 && code < 300) {
                LOG.fine("Email sent via BREVO to: " + to);
            } else {
                LOG.warning("BREVO send failed with status " + code + ". Falling back to SMTP.");
                sendViaSmtp(to, subject, htmlContent);
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, "BREVO send failed: " + e.getMessage() + ". Falling back to SMTP.", e);
            sendViaSmtp(to, subject, htmlContent);
        }
    }

    private static void sendViaSmtp(String to, String subject, String htmlContent) {
        final String from = cfg("mail.user", "MAIL_USER");
        final String password = cfg("mail.pass", "MAIL_PASS");
        if (from == null || from.isBlank() || password == null || password.isBlank()) {
            LOG.warning("SMTP credentials missing (MAIL_USER/MAIL_PASS). Skip sending mail.");
            return;
        }
        final String host = defaultIfBlank(cfg("mail.smtp.host", "MAIL_SMTP_HOST"), "smtp.gmail.com");
        final String port = defaultIfBlank(cfg("mail.smtp.port", "MAIL_SMTP_PORT"), "587");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

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

    private static String cfg(String sysProp, String envVar) {
        String v = System.getProperty(sysProp);
        if (v != null && !v.isBlank()) return v.trim();
        v = System.getenv(envVar);
        return v != null ? v.trim() : null;
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return (value == null || value.isBlank()) ? defaultValue : value;
    }
}
