package potenday.backend.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Dialogue {

    String speaker;
    Long startTime;
    Long endTime;
    String text;

    public static Dialogue create(String speaker, Long startTime, Long endTime, String test) {
        return new Dialogue(speaker, startTime, endTime, test);
    }

    @Override
    public String toString() {
        return String.format("%s: %s", speaker, text);
    }

    public Dialogue addTime(Long time) {
        this.startTime += time;
        this.endTime += time;
        return this;
    }

}
