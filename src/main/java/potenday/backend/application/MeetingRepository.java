package potenday.backend.application;

import org.springframework.stereotype.Repository;
import potenday.backend.domain.Meeting;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetingRepository {

    void saveAll(List<Meeting> meetings);

    void save(Meeting meeting);

    Optional<Meeting> findById(String id);

    List<Meeting> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted);

    List<Meeting> findByOwnerIdAndIsDeletedAndIdGreaterThan(
        String ownerId,
        Boolean isDeleted,
        String lastId,
        Integer limit
    );


}
