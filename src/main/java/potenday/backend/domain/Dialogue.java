package potenday.backend.domain;

import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record Dialogue(
    String speaker,
    Long startTime,
    Long endTime,
    String text
) {

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
