package potenday.backend.web.request;

import java.util.List;

public record MeetingRestoreRequest(
    List<String> meetingIds
) {

}
