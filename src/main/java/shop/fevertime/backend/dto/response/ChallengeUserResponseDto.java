package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import shop.fevertime.backend.domain.ChallengeHistory;
import shop.fevertime.backend.domain.User;

import java.util.List;

@Setter
@Getter
public class ChallengeUserResponseDto {
    // User
    private Long userId;
    private String username;
    private String userimgUrl;
    private int certiCount; // 인증 횟수
    private int retryCount; // 재도전 횟수
    private String userStartDate; // 챌린지 미션 시작 날짜
    private String userMissionDate; // 챌린지 미션 종료 날짜
    private String firstWeekMission; // 첫 주차 미션 달성 여부 "YES OR NO"

    // user ChallengeHistory List
    private String challengeStatus;

    // Certification List
    private List<CertificationResponseDto> certifies;


    public ChallengeUserResponseDto(User user, List<CertificationResponseDto> certifies, ChallengeHistory userHistory) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.userimgUrl = user.getImgUrl();
        this.certifies = certifies;
        this.challengeStatus = userHistory.getChallengeStatus().toString();
        this.certiCount = certifies.size();
        this.retryCount = userHistory.getRetryCount();
        this.userStartDate = userHistory.getCreatedDate().toString();
        this.userMissionDate = userHistory.getMissionDate().toString();
        this.firstWeekMission = userHistory.getFirstWeekMission().toString();

    }
}
