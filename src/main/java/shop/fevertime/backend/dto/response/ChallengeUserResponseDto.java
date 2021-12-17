package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import shop.fevertime.backend.domain.User;

import java.util.List;

@Setter
@Getter
public class ChallengeUserResponseDto {
    // User
    private Long userId;
    private String username;
    private String userimgUrl;

    // Certification List
    private List<CertificationResponseDto> certifies;

    // user ChallengeHistory
    private ChallengeHistoryResponseDto userHistory;

    public ChallengeUserResponseDto(
            User user,
            List<CertificationResponseDto> certifies,
            ChallengeHistoryResponseDto userHistory
    ) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.userimgUrl = user.getImgUrl();
        this.certifies = certifies;
        this.userHistory = userHistory;
    }
}
