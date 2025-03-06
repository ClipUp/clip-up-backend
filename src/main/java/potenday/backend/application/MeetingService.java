package potenday.backend.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import potenday.backend.application.dto.ChatResponse;
import potenday.backend.domain.Meeting;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MeetingService {

    private final MeetingReader meetingReader;
    private final UserReader userReader;
    private final MeetingWriter meetingWriter;
    private final MeetingChat meetingChat;

    public Meeting crateMeeting(String userId, MultipartFile audioFile, int audioFileDuration) {
        userReader.validateExistUser(userId);
        return meetingWriter.create(userId, audioFile, audioFileDuration);
    }

    public Meeting updateMeeting(String meetingId, String userId, String title) {
        return meetingWriter.update(meetingId, userId, title);
    }

    public void deleteMeetings(String userId, List<String> meetingIds) {
        meetingWriter.delete(userId, meetingIds);
    }

    public void restoreMeetings(String userId, List<String> meetingIds) {
        meetingWriter.restore(userId, meetingIds);
    }

    public Meeting readMeeting(String meetingId, String userId) {
        return meetingReader.read(userId, meetingId);
    }

    public Meeting readMeeting(String meetingId) {
        return meetingReader.read(meetingId);
    }

    public List<Meeting> readAllMeetings(String userId, String lastMeetingId, Integer limit) {
        return meetingReader.readAll(userId, lastMeetingId, limit);
    }

    public List<Meeting> readAllDeletedMeetings(String userId, String lastMeetingId, Integer limit) {
        return meetingReader.readAllDeleted(userId, lastMeetingId, limit);
    }

    public ChatResponse chat(String meetingId, String question, String sessionId) {
        return meetingChat.chat(meetingId, question, sessionId);
    }

    public Meeting migrate(String meetingId) {
        return meetingWriter.migrate(meetingId);
    }

}
