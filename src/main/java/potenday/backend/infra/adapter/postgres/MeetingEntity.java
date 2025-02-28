package potenday.backend.infra.adapter.postgres;

import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import potenday.backend.domain.Dialogue;
import potenday.backend.domain.Meeting;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "meeting")
class MeetingEntity {

    @Id
    private String id;
    private String ownerId;
    private String title;
    private String audioFileUrl;
    private Integer audioFileDuration;
    @Convert(converter = ScriptConverter.class)
    private List<Dialogue> script;
    private String minutes;
    private Long createTime;
    private Long updateTime;
    private Boolean isDeleted;

    static MeetingEntity from(Meeting meeting) {
        return MeetingEntity.builder()
            .id(meeting.id())
            .ownerId(meeting.ownerId())
            .title(meeting.title())
            .audioFileUrl(meeting.audioFileUrl())
            .audioFileDuration(meeting.audioFileDuration())
            .script(meeting.script())
            .minutes(meeting.minutes())
            .createTime(meeting.createTime())
            .updateTime(meeting.updateTime())
            .build();
    }

    Meeting toMeeting() {
        return Meeting.builder()
            .id(id)
            .ownerId(ownerId)
            .title(title)
            .audioFileUrl(audioFileUrl)
            .audioFileDuration(audioFileDuration)
            .script(script)
            .minutes(minutes)
            .createTime(createTime)
            .updateTime(updateTime)
            .build();
    }

}
