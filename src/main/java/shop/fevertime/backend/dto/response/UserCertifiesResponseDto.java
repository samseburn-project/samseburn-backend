package shop.fevertime.backend.dto.response;

import lombok.Getter;
import lombok.Setter;
import shop.fevertime.backend.domain.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class UserCertifiesResponseDto {
    private Long id;
    private String username;
    private String imgUrl;
    private int certiCount;
    private FirstWeekMission firstWeekMission;
    private List<CertificationResponseDto> certifies = new ArrayList<>();

    public UserCertifiesResponseDto(User user, List<Certification> certifies, FirstWeekMission firstWeekMission) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.imgUrl = user.getImgUrl();

        // 유저 인증 정보
        certifies.stream()
                .map(CertificationResponseDto::new)
                .forEach(certificationResponseDto -> this.certifies.add(certificationResponseDto));

        this.certiCount = certifies.size();
        this.firstWeekMission = firstWeekMission;
    }

}
