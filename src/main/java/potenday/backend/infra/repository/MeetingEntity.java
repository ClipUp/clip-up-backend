package potenday.backend.infra.repository;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Builder.Default;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import potenday.backend.domain.Dialogue;
import potenday.backend.domain.Meeting;

import java.util.ArrayList;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@Document
class MeetingEntity {

    @Id
    private String id;
    private String ownerId;
    private String title;
    private String audioFileUrl;
    private Integer audioFileDuration;
    @Default
    private List<Dialogue> script = new ArrayList<>();
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
            .createTime(createTime)
            .updateTime(updateTime)
            .build();
    }

}
