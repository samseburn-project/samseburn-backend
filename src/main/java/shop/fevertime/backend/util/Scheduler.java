package shop.fevertime.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.fevertime.backend.domain.Challenge;
import shop.fevertime.backend.domain.ChallengeHistory;
import shop.fevertime.backend.domain.ChallengeProgress;
import shop.fevertime.backend.exception.ApiRequestException;
import shop.fevertime.backend.repository.CertificationRepository;
import shop.fevertime.backend.repository.ChallengeHistoryRepository;
import shop.fevertime.backend.repository.ChallengeRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

@RequiredArgsConstructor
@Component
public class Scheduler {

    private final ChallengeRepository challengeRepository;
    private final ChallengeHistoryRepository challengeHistoryRepository;
    private final CertificationRepository certificationRepository;

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateChallengeProcess() throws ApiRequestException {
        for (Challenge challenge : challengeRepository.findAllByChallengeProgress(ChallengeProgress.INPROGRESS)) {
            if (LocalDate.now().isAfter(challenge.getEndDate().toLocalDate())) {
                challenge.stopChallnegeProgress();
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateChallengeStatus() throws ApiRequestException {
        // 챌린지 mission_date가 됐을 때, 인증 횟수가 3회 넘지 않으면
        for (ChallengeHistory challengeHistory : challengeHistoryRepository.findAllByMissionDateBefore(LocalDate.now())) {
            int certiCount = certificationRepository.findAllByChallenge(challengeHistory.getChallenge()).size();
            if (certiCount < 3) {
                challengeHistory.fail(); // JOIN -> RETRY
            } else {
                challengeHistory.cancel(); // JOIN -> FAIL
            }
        }
    }
}
