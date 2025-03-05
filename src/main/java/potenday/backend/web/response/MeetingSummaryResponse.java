package potenday.backend.web.response;

import potenday.backend.domain.Meeting;

public record MeetingSummaryResponse(
    String id,
    String title,
    Integer audioFileDuration,
    Long creatTime
) {

    public static MeetingSummaryResponse from(Meeting meeting) {
        return new MeetingSummaryResponse(
            meeting.getId(),
            meeting.getTitle(),
            meeting.getAudioFileDuration(),
            meeting.getCreateTime()
        );
    }

}
