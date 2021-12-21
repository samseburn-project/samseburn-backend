package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import shop.fevertime.backend.domain.Category;
import shop.fevertime.backend.domain.Challenge;
import shop.fevertime.backend.domain.ChallengeProgress;
import shop.fevertime.backend.domain.LocationType;

import java.time.format.DateTimeFormatter;

/**
 * 유저 페이지에 챌린지 반환 Dto
 */
@Getter
@Setter
public class UserChallengeResponseDto {
    private Long challengeId;
    private String title;
    private String description;
    private String challengeStartDate;
    private String challengeEndDate;
    private int limitPerson;
    private LocationType locationType;
    private String address;
    private String imgUrl;
    private String category;
    private ChallengeProgress challengeProgress;

    public UserChallengeResponseDto(Challenge challenge) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.challengeStartDate = challenge.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.challengeEndDate = challenge.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.limitPerson = challenge.getLimitPerson();
        this.locationType = challenge.getLocationType();
        this.address = challenge.getAddress();
        this.imgUrl = challenge.getImgUrl();
        this.category = challenge.getCategory().getName();
        this.challengeProgress = challenge.getChallengeProgress();

    }
}
