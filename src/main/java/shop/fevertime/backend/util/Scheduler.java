package shop.fevertime.backend.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import shop.fevertime.backend.domain.Certification;
import shop.fevertime.backend.domain.Challenge;
import shop.fevertime.backend.domain.ChallengeHistory;
import shop.fevertime.backend.domain.ChallengeProgress;
import shop.fevertime.backend.exception.ApiRequestException;
import shop.fevertime.backend.repository.CertificationRepository;
import shop.fevertime.backend.repository.ChallengeHistoryRepository;
import shop.fevertime.backend.repository.ChallengeRepository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class Scheduler {

    private final ChallengeRepository challengeRepository;
    private final ChallengeHistoryRepository challengeHistoryRepository;
    private final CertificationRepository certificationRepository;

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateChallengeProcess() throws ApiRequestException {
        for (Challenge challenge : challengeRepository.findAllByChallengeProgress(ChallengeProgress.INPROGRESS)) {
            if (LocalDate.now().isAfter(challenge.getEndDate().toLocalDate())) {
                challenge.stopChallnegeProgress();
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateChallengeStatus() throws ApiRequestException {
        // 챌린지 mission_date가 됐을 때, 인증 횟수가 3회 넘지 않으면 ( 재도전 기회 있으면 retry 없으면 fail ) -> 해당 인증도 삭제
        for (ChallengeHistory challengeHistory : challengeHistoryRepository.findAllByMissionDateBefore(LocalDateTime.now())) {
            List<Certification> certiCount = certificationRepository.findAllByChallengeAndUser(challengeHistory.getChallenge(), challengeHistory.getUser());
            if (certiCount.size() < 3 && challengeHistory.getRetryCount() < 3) {
                challengeHistory.fail(); // JOIN -> RETRY
                certificationRepository.deleteAll(certiCount);
            } else if (certiCount.size() < 3 && challengeHistory.getRetryCount() >= 3) {
                challengeHistory.cancel(); // JOIN -> FAIL
                certificationRepository.deleteAll(certiCount);
            }
        }
    }


    @Scheduled(cron = "0 33 14 * * *", zone = "Asia/Seoul")
    public void test() throws ApiRequestException {

        log.error("스케줄러 AM 14:33" + new Date());
    }
}

