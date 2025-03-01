package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.MeetingRepository;
import potenday.backend.domain.Meeting;
import potenday.backend.support.exception.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
class MeetingReader {

    private final MeetingRepository meetingRepository;

    Meeting read(String userId, String id) {
        Meeting existMeeting = meetingRepository.findById(id).orElseThrow(ErrorCode.MEETING_NOT_FOUNDED::toException);
        if (!existMeeting.ownerId().equals(userId)) {
            throw ErrorCode.FORBIDDEN.toException();
        }
        return existMeeting;
    }

    Meeting read(String id) {
        return meetingRepository.findById(id).orElseThrow(ErrorCode.MEETING_NOT_FOUNDED::toException);
    }

    List<Meeting> readAll(String userId, String lastId, Integer limit) {
        return meetingRepository.findByOwnerIdAndIsDeletedAndIdGreaterThan(userId, false, lastId, limit);
    }

    List<Meeting> readAllDeleted(String userId, String lastId, Integer limit) {
        return meetingRepository.findByOwnerIdAndIsDeletedAndIdGreaterThan(userId, true, lastId, limit);
    }

}
