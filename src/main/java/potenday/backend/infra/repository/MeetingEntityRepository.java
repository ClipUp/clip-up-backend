package potenday.backend.infra.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

interface MeetingEntityRepository extends MongoRepository<MeetingEntity, String> {

    List<MeetingEntity> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted);

}
