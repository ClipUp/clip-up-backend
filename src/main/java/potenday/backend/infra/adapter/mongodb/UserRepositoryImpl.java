package potenday.backend.infra.adapter.mongodb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import potenday.backend.application.port.UserRepository;
import potenday.backend.domain.User;

import java.util.Optional;

@RequiredArgsConstructor
@Repository
class UserRepositoryImpl implements UserRepository {

    private final UserEntityRepository userEntityRepository;

    @Override
    public void save(User user) {
        userEntityRepository.save(UserEntity.from(user));
    }

    @Override
    public boolean existsById(String id) {
        return userEntityRepository.existsById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }

    @Override
    public Optional<User> findById(String id) {
        return userEntityRepository.findById(id).map(UserEntity::toUser);
    }

}
