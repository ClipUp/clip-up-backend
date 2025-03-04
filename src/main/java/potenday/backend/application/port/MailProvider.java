package potenday.backend.application.port;

public interface MailProvider {

    void send(String from, String to, String subject, String message);

}
