package potenday.backend.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import potenday.backend.domain.Meeting;

import java.util.List;

public interface MongoMeetingRepository extends MongoRepository<Meeting, String> {

    List<Meeting> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted);

}
