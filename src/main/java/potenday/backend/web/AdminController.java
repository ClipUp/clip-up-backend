package potenday.backend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import potenday.backend.application.MeetingService;
import potenday.backend.web.response.MeetingResponse;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
class AdminController {

    private final MeetingService meetingService;

    @GetMapping("/meetings/{meetingId}")
    MeetingResponse readMeeting(@PathVariable String meetingId) {
        return MeetingResponse.from(meetingService.readMeeting(meetingId));
    }

    @PostMapping("/meetings/{meetingId}/migration")
    MeetingResponse migrate(@PathVariable String meetingId) {
        return MeetingResponse.from(meetingService.migrate(meetingId));
    }

}
