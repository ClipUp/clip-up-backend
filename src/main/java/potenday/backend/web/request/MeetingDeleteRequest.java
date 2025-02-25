package potenday.backend.web.request;

import java.util.List;

public record MeetingDeleteRequest(
    List<String> meetingIds
) {

}
