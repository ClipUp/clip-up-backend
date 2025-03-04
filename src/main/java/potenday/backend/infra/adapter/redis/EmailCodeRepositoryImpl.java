package potenday.backend.infra.adapter.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import potenday.backend.application.port.EmailCodeRepository;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Component
class EmailCodeRepositoryImpl implements EmailCodeRepository {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void save(String email, String code, Duration duration) {
        stringRedisTemplate.opsForValue().set(email, code, duration);
    }

    @Override
    public Optional<String> findByEmail(String email) {
        return Optional.ofNullable(stringRedisTemplate.opsForValue().get(email));
    }

    @Override
    public void delete(String email) {

    }


}
