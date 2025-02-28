package potenday.backend.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import potenday.backend.application.MeetingService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/test")
public class TestController {

    private final MeetingService meetingService;

    @PostMapping("/{meetingId}")
    void update(@RequestBody Request request, @PathVariable String meetingId) {
        meetingService.updateMinutes(meetingId, request.minutes);
    }

    record Request(
        String minutes
    ) {

    }

}
