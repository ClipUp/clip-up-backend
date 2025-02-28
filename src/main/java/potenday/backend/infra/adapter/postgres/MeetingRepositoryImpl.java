package potenday.backend.infra.adapter.postgres;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import potenday.backend.application.port.MeetingRepository;
import potenday.backend.domain.Meeting;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
class MeetingRepositoryImpl implements MeetingRepository {

    private final MeetingEntityRepository meetingEntityRepository;

    @Override
    public void saveAll(List<Meeting> meetings) {
        meetingEntityRepository.saveAll(meetings.stream().map(MeetingEntity::from).toList());
    }

    @Override
    public void save(Meeting meeting) {
        meetingEntityRepository.save(MeetingEntity.from(meeting));
    }

    @Override
    public Optional<Meeting> findById(String id) {
        return meetingEntityRepository.findById(id).map(MeetingEntity::toMeeting);
    }

    @Override
    public List<Meeting> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted) {
        return meetingEntityRepository.findAllByIdInAndOwnerIdAndIsDeleted(id, ownerId, isDeleted)
            .stream()
            .map(MeetingEntity::toMeeting)
            .toList();
    }

    @Override
    public List<Meeting> findByOwnerIdAndIsDeletedAndIdGreaterThan(
        String ownerId,
        Boolean isDeleted,
        String lastId,
        Integer limit
    ) {
        List<MeetingEntity> meetings = lastId == null ? meetingEntityRepository.findAllByOwnerIdAndIsDeleted(ownerId, isDeleted, limit) : meetingEntityRepository.findAllByOwnerIdAndIsDeletedAndIdGreaterThan(ownerId, isDeleted, limit, lastId);

        return meetings.stream()
            .map(MeetingEntity::toMeeting)
            .toList();
    }

}
