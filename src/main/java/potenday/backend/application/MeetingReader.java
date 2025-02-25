package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import potenday.backend.domain.Meeting;
import potenday.backend.domain.repository.MeetingRepository;
import potenday.backend.support.ErrorCode;

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

    List<Meeting> readAll(String userId, String lastId, Integer limit) {
        return meetingRepository.findByOwnerIdAndIsDeletedAndIdGreaterThan(userId, false, lastId, limit);
    }

    List<Meeting> readAllDeleted(String userId, String lastId, Integer limit) {
        return meetingRepository.findByOwnerIdAndIsDeletedAndIdGreaterThan(userId, true, lastId, limit);
    }

}
