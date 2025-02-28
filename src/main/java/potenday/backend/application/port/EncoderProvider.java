package potenday.backend.application.port;

public interface EncoderProvider {

    String encode(String text);

    boolean matches(String text, String encodedText);

}
