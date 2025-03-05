package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.domain.Meeting;
import potenday.backend.domain.repository.MeetingRepository;
import potenday.backend.support.exception.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
class MeetingWriter {

    private final IdProvider idProvider;
    private final ClockProvider clockProvider;
    private final MeetingRepository meetingRepository;
    private final STTProcessor sttProcessor;
    private final MinutesProcessor minutesProcessor;

    Meeting create(String userId, MultipartFile audioFile, Integer audioFileDuration) {
        String id = idProvider.nextId();

        STTProcessor.Result sttResult = sttProcessor.convert(id, audioFile);
        String minutes = minutesProcessor.generate(sttResult.script());

        Meeting newMeeting = Meeting.create(id, userId, sttResult.audioFileUrl(), audioFileDuration, sttResult.script(), minutes, clockProvider.millis());
        meetingRepository.save(newMeeting);

        return newMeeting;
    }

    @Transactional
    Meeting update(String id, String userId, String title) {
        Meeting existMeeting = findMeetingById(id);
        Meeting updatedMeeting = existMeeting.update(userId, title, clockProvider.millis());
        meetingRepository.save(updatedMeeting);
        return updatedMeeting;
    }

    @Transactional
    void delete(String userId, List<String> ids) {
        List<Meeting> meetings = meetingRepository.findAllByIdInAndOwnerIdAndIsDeleted(ids, userId, false)
            .stream()
            .map(meeting -> meeting.delete(userId, clockProvider.millis()))
            .toList();
        meetingRepository.saveAll(meetings);
    }

    @Transactional
    void restore(String userId, List<String> ids) {
        List<Meeting> meetings = meetingRepository.findAllByIdInAndOwnerIdAndIsDeleted(ids, userId, true)
            .stream()
            .map(meeting -> meeting.restore(userId, clockProvider.millis()))
            .toList();
        meetingRepository.saveAll(meetings);
    }

    private Meeting findMeetingById(String id) {
        return meetingRepository.findById(id)
            .orElseThrow(ErrorCode.MEETING_NOT_FOUNDED::toException);
    }

}
