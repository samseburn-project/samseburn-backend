package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import shop.fevertime.backend.domain.FirstWeekMission;
import shop.fevertime.backend.domain.User;

@Getter
@Setter
public class UserChallengeStatusResponseDto {
    private User user;
    private FirstWeekMission firstWeekMission;

    public UserChallengeStatusResponseDto(User user, FirstWeekMission firstWeekMission) {
        this.user = user;
        this.firstWeekMission = firstWeekMission;
    }
}
