package potenday.backend.infra.adapter.postgres;

import org.springframework.data.jpa.repository.JpaRepository;

interface UserEntityRepository extends JpaRepository<UserEntity, String> {

    boolean existsByEmail(String email);

}
