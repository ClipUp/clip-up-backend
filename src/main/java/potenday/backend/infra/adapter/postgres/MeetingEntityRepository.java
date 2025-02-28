package potenday.backend.infra.adapter.postgres;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

interface MeetingEntityRepository extends JpaRepository<MeetingEntity, String> {

    @Query(
        value = "SELECT meeting.id, meeting.title, meeting.audioFileDuration, meeting.createTime " +
            "FROM meeting " +
            "WHERE owner_id = :ownerId AND is_deleted = :isDeleted " +
            "ORDER BY id DESC LIMIT :limit",
        nativeQuery = true
    )
    List<MeetingEntity> findByOwnerIdAndIsDeletedAndId(
        @Param("ownerId") String ownerId,
        @Param("isDeleted") Boolean isDeleted,
        @Param("limit") Integer limit
    );

    @Query(
        value = "SELECT meeting.id, meeting.title, meeting.audioFileDuration, meeting.createTime " +
            "FROM meeting " +
            "WHERE owner_id = :ownerId AND is_deleted = :isDeleted AND id < :lastId " +
            "ORDER BY id DESC LIMIT :limit",
        nativeQuery = true
    )
    List<MeetingEntity> findByOwnerIdAndIsDeletedAndIdGreaterThan(
        @Param("ownerId") String ownerId,
        @Param("isDeleted") Boolean isDeleted,
        @Param("limit") Integer limit,
        @Param("lastId") String lastId
    );

    List<MeetingEntity> findAllByIdInAndOwnerIdAndIsDeleted(List<String> id, String ownerId, Boolean isDeleted);

}
