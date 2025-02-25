package potenday.backend.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class Dialogue {

    String speaker;
    Long startTime;
    Long endTime;
    String text;

    public static Dialogue create(String speaker, Long startTime, Long endTime, String test) {
        return Dialogue.builder()
            .speaker(speaker)
            .startTime(startTime)
            .endTime(endTime)
            .text(test)
            .build();
    }

    @Override
    public String toString() {
        return String.format("%s: %s", speaker, text);
    }

}
