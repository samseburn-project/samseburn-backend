package shop.fevertime.kotlin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.fevertime.kotlin.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(String kakaoId);

}
