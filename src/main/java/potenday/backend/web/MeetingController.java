package potenday.backend.web;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.MeetingService;
import potenday.backend.web.request.MeetingDeleteRequest;
import potenday.backend.web.request.MeetingRestoreRequest;
import potenday.backend.web.request.MeetingUpdateRequest;
import potenday.backend.web.response.MeetingResponse;
import potenday.backend.web.response.MeetingSummaryResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/meetings")
class MeetingController {

    private final MeetingService meetingService;

    @GetMapping
    List<MeetingSummaryResponse> readAllMeetings(
        @AuthenticationPrincipal String userId,
        @RequestParam(required = false) String lastMeetingId,
        @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        return meetingService.readAllMeetings(userId, lastMeetingId, limit)
            .stream()
            .map(MeetingSummaryResponse::from)
            .toList();
    }

    @GetMapping("/trash")
    List<MeetingSummaryResponse> readAllDeletedMeetings(
        @AuthenticationPrincipal String userId,
        @RequestParam(required = false) String lastMeetingId,
        @RequestParam(required = false, defaultValue = "10") Integer limit
    ) {
        return meetingService.readAllDeletedMeetings(userId, lastMeetingId, limit)
            .stream()
            .map(MeetingSummaryResponse::from)
            .toList();
    }

    @GetMapping("/{meetingId}")
    MeetingResponse readMeeting(@PathVariable String meetingId, @AuthenticationPrincipal String userId) {
        return MeetingResponse.from(meetingService.readMeeting(meetingId, userId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    MeetingResponse createMeeting(
        @AuthenticationPrincipal String userId,
        MultipartFile audioFile,
        @RequestParam Integer audioFileDuration
    ) {
        return MeetingResponse.from(meetingService.crateMeeting(userId, audioFile, audioFileDuration));
    }

    @PutMapping("/{meetingId}")
    MeetingResponse updateMeeting(
        @PathVariable String meetingId, @AuthenticationPrincipal String userId, @RequestBody @Valid
        MeetingUpdateRequest request
    ) {
        return MeetingResponse.from(meetingService.updateMeeting(meetingId, userId, request.title()));
    }

    @DeleteMapping
    void deleteMeeting(@AuthenticationPrincipal String userId, @RequestBody @Valid MeetingDeleteRequest request) {
        meetingService.deleteMeetings(userId, request.meetingIds());
    }

    @DeleteMapping("/trash")
    void restoreMeeting(@AuthenticationPrincipal String userId, @RequestBody @Valid MeetingRestoreRequest request) {
        meetingService.restoreMeetings(userId, request.meetingIds());
    }

}
