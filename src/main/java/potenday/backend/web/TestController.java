package potenday.backend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import potenday.backend.application.MeetingService;
import potenday.backend.web.response.MeetingResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
class TestController {

    private final MeetingService meetingService;

    @GetMapping("/{meetingId}")
    MeetingResponse readMeeting(@PathVariable String meetingId) {
        return MeetingResponse.from(meetingService.readMeeting(meetingId));
    }

}
