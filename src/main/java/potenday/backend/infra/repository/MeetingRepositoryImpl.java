package potenday.backend.infra.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import potenday.backend.application.MeetingRepository;
import potenday.backend.domain.Meeting;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
class MeetingRepositoryImpl implements MeetingRepository {

    private final MongoTemplate mongoTemplate;
    private final MeetingEntityRepository mongoMeetingRepository;

    @Override
    public void saveAll(List<Meeting> meetings) {
        mongoMeetingRepository.saveAll(meetings.stream().map(MeetingEntity::from).toList());
    }

    @Override
    public void save(Meeting meeting) {
        mongoMeetingRepository.save(MeetingEntity.from(meeting));
    }

    @Override
    public Optional<Meeting> findById(String id) {
        return mongoMeetingRepository.findById(id).map(MeetingEntity::toMeeting);
    }

    @Override
    public List<Meeting> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted) {
        return mongoMeetingRepository.findAllByIdInAndOwnerIdAndIsDeleted(id, ownerId, isDeleted)
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
        Query query = new Query();
        query.addCriteria(Criteria.where("ownerId").is(ownerId)
            .and("isDeleted").is(isDeleted));

        if (lastId != null) {
            query.addCriteria(Criteria.where("_id").lt(lastId));
        }

        query.limit(limit);
        query.with(Sort.by(Sort.Order.desc("_id")));

        return mongoTemplate.find(query, MeetingEntity.class).stream().map(MeetingEntity::toMeeting).toList();
    }

}
