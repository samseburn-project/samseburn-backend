package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import shop.fevertime.backend.domain.Certification;
import shop.fevertime.backend.domain.FirstWeekMission;
import shop.fevertime.backend.domain.User;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserChallengeStatusResponseDto {
    private User user;
    private FirstWeekMission firstWeekMission;
    private List<Certification> certifies;

    public UserChallengeStatusResponseDto(User user, FirstWeekMission firstWeekMission, List<Certification> certifies) {
        this.user = user;
        this.firstWeekMission = firstWeekMission;
        this.certifies = certifies;
    }
}
