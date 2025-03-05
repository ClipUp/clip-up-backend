package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.domain.Meeting;
import potenday.backend.domain.repository.MeetingRepository;
import potenday.backend.support.exception.ErrorCode;

import java.util.List;

@RequiredArgsConstructor
@Component
class MeetingReader {

    private final MeetingRepository meetingRepository;

    Meeting read(String userId, String id) {
        Meeting existMeeting = meetingRepository.findById(id).orElseThrow(ErrorCode.MEETING_NOT_FOUNDED::toException);
        if (!existMeeting.getOwnerId().equals(userId)) {
            throw ErrorCode.FORBIDDEN.toException();
        }
        return existMeeting;
    }

    Meeting read(String id) {
        return meetingRepository.findById(id).orElseThrow(ErrorCode.MEETING_NOT_FOUNDED::toException);
    }

    List<Meeting> readAll(String userId, String lastId, Integer limit) {
        return lastId == null
            ? meetingRepository.findAllByOwnerIdAndIsDeleted(userId, false, limit)
            : meetingRepository.findAllByOwnerIdAndIsDeletedAndIdGreaterThan(userId, false, limit, lastId);
    }

    List<Meeting> readAllDeleted(String userId, String lastId, Integer limit) {
        return lastId == null
            ? meetingRepository.findAllByOwnerIdAndIsDeleted(userId, true, limit)
            : meetingRepository.findAllByOwnerIdAndIsDeletedAndIdGreaterThan(userId, true, limit, lastId);
    }

}
