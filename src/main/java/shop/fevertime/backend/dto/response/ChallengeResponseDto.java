package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import shop.fevertime.backend.domain.Challenge;
import shop.fevertime.backend.domain.ChallengeProgress;
import shop.fevertime.backend.domain.LocationType;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
public class ChallengeResponseDto {

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
    private long participants;

    public ChallengeResponseDto(Challenge challenge, long participants) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.imgUrl = challenge.getImgUrl();
        this.challengeStartDate = challenge.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.challengeEndDate = challenge.getEndDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.limitPerson = challenge.getLimitPerson();
        this.locationType = challenge.getLocationType();
        this.address = challenge.getAddress();
        this.category = new CategoryResponseDto(challenge.getCategory()).getName();
        this.challengeProgress = challenge.getChallengeProgress();
        this.participants = participants;
    }
}
