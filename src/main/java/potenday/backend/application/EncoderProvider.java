package potenday.backend.application;

public interface EncoderProvider {

    String encode(String text);

    boolean matches(String text, String encodedText);

}
