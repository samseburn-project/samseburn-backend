package shop.fevertime.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shop.fevertime.backend.domain.*;
import shop.fevertime.backend.dto.response.*;
import shop.fevertime.backend.exception.ApiRequestException;
import shop.fevertime.backend.repository.CertificationRepository;
import shop.fevertime.backend.repository.ChallengeHistoryRepository;
import shop.fevertime.backend.repository.ChallengeRepository;
import shop.fevertime.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChallengeHistoryService {

    private final ChallengeHistoryRepository challengeHistoryRepository;
    private final ChallengeRepository challengeRepository;
    private final CertificationRepository certificationRepository;
    private final UserRepository userRepository;
    private final CertificationService certificationService;

    @Transactional
    public ChallengeUserResponseDto getChallengeHistoryUser(Long challengeId, User user) {
        // 챌린지 찾기
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );
        // 유저가 챌린지 인증한 리스트 찾기
        List<CertificationResponseDto> certifies = certificationRepository.findAllByChallengeAndUser(challenge, user).stream()
                .map(CertificationResponseDto::new)
                .collect(Collectors.toList());
        // 챌린지 참여 내역
        List<ChallengeHistoryResponseDto> userHistories = challengeHistoryRepository.findAllByChallengeAndUser(challenge, user).stream()
                .map(ChallengeHistoryResponseDto::new)
                .collect(Collectors.toList());

        return new ChallengeUserResponseDto(user, certifies, userHistories);
    }

    @Transactional
    public List<UserCertifiesResponseDto> getChallengeHistoryUsers(Long challengeId) {
        // 챌린지 찾기
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );
        //히스토리에서 해당 챌린지에 조인한 데이터 가져옴 -> 해당 유저 리스트 가져옴
        List<ChallengeHistory> histories = challengeHistoryRepository.findAllByChallengeAndChallengeStatus(challenge, ChallengeStatus.JOIN);

        List<UserChallengeStatusResponseDto> userList = new ArrayList<>();

        //찾아온 히스토리 데이터에서 유저와 1주차 미션 여부 가져와야할듯?!
        for (ChallengeHistory history : histories) {
            userList.add(new UserChallengeStatusResponseDto(history.getUser(), history.getFirstWeekMission()));
        }

        return userList.stream().
                map(UserChallengeStatusResponseDto -> new UserCertifiesResponseDto(UserChallengeStatusResponseDto.getUser(),
                        UserChallengeStatusResponseDto.getUser().getCertificationList(),
                        UserChallengeStatusResponseDto.getFirstWeekMission()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void joinChallenge(Long challengeId, User user) throws ApiRequestException {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );

        if (challenge.getChallengeProgress() == ChallengeProgress.STOP) {
            throw new ApiRequestException("종료된 챌린지에 참여할 수 없습니다.");
        }

        //해당 챌린지와 유저로 히스토리 찾아와서 retry개수 컬럼 값 확인


        LocalDateTime now = LocalDateTime.now();
        ChallengeHistory challengeHistory = new ChallengeHistory(
                user,
                challenge,
                now,
                now.plusDays(7),
                ChallengeStatus.JOIN,
                FirstWeekMission.NO);

        challengeHistoryRepository.save(challengeHistory);
    }

    @Transactional
    public void cancelChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );

        if (challenge.getChallengeProgress() == ChallengeProgress.STOP) {
            throw new ApiRequestException("종료된 챌린지에 참여 취소할 수 없습니다.");
        }
        certificationRepository.findAllByChallengeAndUser(challenge, user)
                .forEach(certi -> certificationService.deleteCertification(certi.getId(), user));

        //참여 취소시 해당 챌린지히스토리 삭제 (완전 다)
        challengeHistoryRepository.delete(
                challengeHistoryRepository.findByChallengeAndUser(challenge, user));
    }

    // 작업중
    public List<UserChallengeInfoDto> getChallengesByUser(User user) {

        return challengeHistoryRepository.findAllByUser(user).stream()
                .map(challengeHistory -> new UserChallengeInfoDto(
                        challengeHistory.getChallenge(), challengeHistory,
                        certificationRepository.findAllByChallengeAndUser(challengeHistory.getChallenge(), user),
                        challengeHistoryRepository.countChallengeHistoriesByChallengeAndUserAndChallengeStatus(challengeHistory.getChallenge(), user, ChallengeStatus.FAIL)

                )).collect(Collectors.toList());
    }


    @Transactional
    public void continueChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );
        ChallengeHistory history = challengeHistoryRepository.findByChallengeAndUser(challenge, user);

        //히스토리에 FirstWeekMission.YES로 업데이트
        history.continueChallenge();
    }

    @Transactional
    public void stopChallenge(Long challengeId, User user) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(
                () -> new ApiRequestException("해당 챌린지를 찾을 수 없습니다.")
        );
        //그만두기시 해당 챌린지히스토리 삭제 (완전 다)
        challengeHistoryRepository.delete(
                challengeHistoryRepository.findByChallengeAndUser(challenge, user));
    }
}
