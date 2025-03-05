package potenday.backend.web.response;

import potenday.backend.domain.Meeting;

import java.util.List;

public record MeetingResponse(
    String id,
    String title,
    List<DialogueResponse> script,
    String audioFileUrl,
    String minutes,
    Long createTime,
    Long updateTime
) {

    public static MeetingResponse from(Meeting meeting) {
        return new MeetingResponse(
            meeting.getId(),
            meeting.getTitle(),
            meeting.getScript().stream().map(DialogueResponse::from).toList(),
            meeting.getAudioFileUrl(),
            meeting.getMinutes(),
            meeting.getCreateTime(),
            meeting.getUpdateTime()
        );
    }

}
