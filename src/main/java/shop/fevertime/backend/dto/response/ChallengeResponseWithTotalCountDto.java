package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ChallengeResponseWithTotalCountDto {

    private long totalCount;
    private List<ChallengeResponseDto> challengeList = new ArrayList<>();

    public ChallengeResponseWithTotalCountDto(List<ChallengeResponseDto> challengeResponseDtoList, long totalCount) {
        this.totalCount = totalCount;
        this.challengeList = challengeResponseDtoList;
    }
}


