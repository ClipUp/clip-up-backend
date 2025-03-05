package potenday.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import potenday.backend.domain.Meeting;

import java.util.List;

@Repository
public interface MeetingRepository extends JpaRepository<Meeting, String> {

    @Query(
        value = "SELECT id, owner_id, title, audio_file_url, audio_file_duration, script, minutes, create_time, update_time, is_deleted " +
            "FROM clip_up.meeting " +
            "WHERE owner_id = :ownerId AND is_deleted = :isDeleted " +
            "ORDER BY id DESC LIMIT :limit",
        nativeQuery = true
    )
    List<Meeting> findAllByOwnerIdAndIsDeleted(
        @Param("ownerId") String ownerId,
        @Param("isDeleted") Boolean isDeleted,
        @Param("limit") Integer limit
    );

    @Query(
        value = "SELECT id, owner_id, title, audio_file_url, audio_file_duration, script, minutes, create_time, update_time, is_deleted " +
            "FROM clip_up.meeting " +
            "WHERE owner_id = :ownerId AND is_deleted = :isDeleted AND id < :lastId " +
            "ORDER BY id DESC LIMIT :limit",
        nativeQuery = true
    )
    List<Meeting> findAllByOwnerIdAndIsDeletedAndIdGreaterThan(
        @Param("ownerId") String ownerId,
        @Param("isDeleted") Boolean isDeleted,
        @Param("limit") Integer limit,
        @Param("lastId") String lastId
    );

    List<Meeting> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted);

}
