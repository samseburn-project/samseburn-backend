package shop.fevertime.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import shop.fevertime.backend.domain.Challenge;
import shop.fevertime.backend.domain.ChallengeProgress;
import shop.fevertime.backend.domain.User;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findAllByTitleContaining(String title);

    Page<Challenge> findAllByCategoryNameEquals(String category, Pageable pageable);

    Page<Challenge> findAll(Pageable pageable);

    List<Challenge> findAllByUser(User user);

    Optional<Challenge> findByIdAndUser(Long challengeId, User user);

    Page<Challenge> findAllByChallengeProgressAndCategoryNameEquals(ChallengeProgress challengeProgress, String category, Pageable pageable);

    List<Challenge> findAllByChallengeProgress(ChallengeProgress challengeProgress);

    Page<Challenge> findAllByChallengeProgress(ChallengeProgress challengeProgress, Pageable pageable);


}
