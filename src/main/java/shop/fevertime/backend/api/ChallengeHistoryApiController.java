package shop.fevertime.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import shop.fevertime.backend.dto.response.ChallengeUserResponseDto;
import shop.fevertime.backend.dto.response.ResultResponseDto;
import shop.fevertime.backend.dto.response.UserCertifiesResponseDto;
import shop.fevertime.backend.security.UserDetailsImpl;
import shop.fevertime.backend.service.ChallengeHistoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/challenges/{challengeId}")
public class ChallengeHistoryApiController {

    private final ChallengeHistoryService challengeHistoryService;

    /**
     * 해당 챌린지 참여한 유저정보(+챌린지 인증 리스트) 리스트 API
     */
    @GetMapping("/users")
    public List<UserCertifiesResponseDto> getChallengeHistoryUsers(
            @PathVariable Long challengeId
    ) {
        return challengeHistoryService.getChallengeHistoryUsers(challengeId);
    }

    /**
     * 해당 챌린지 참여 기록 리스트
     */
    @GetMapping("/user")
    public ChallengeUserResponseDto getChallengeHistoryUser(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        return challengeHistoryService.getChallengeHistoryUser(challengeId, userDetails.getUser());
    }

    /**
     * 챌린지 참여 API
     */
    @PostMapping("/join")
    public ResultResponseDto joinChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        challengeHistoryService.joinChallenge(challengeId, userDetails.getUser());
        return new ResultResponseDto("success", "챌린지 참여되었습니다.");
    }

    /**
     * 챌린지 참여 취소 API
     */
    @DeleteMapping("/join")
    public ResultResponseDto cancelChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        challengeHistoryService.cancelChallenge(challengeId, userDetails.getUser());
        return new ResultResponseDto("success", "챌린지 참여 취소되었습니다.");
    }

    /**
     * 챌린지 재도전 API
     */
    @PutMapping("/retry")
    public ResultResponseDto retryChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        challengeHistoryService.retryChallenge(challengeId, userDetails.getUser());
        return new ResultResponseDto("success", "재도전 상태로 변경되었습니다.");
    }


    /**
     * 1주차 미션 성공 후 챌린지 계속하기 선택 API
     */
    @PutMapping("/continue")
    public ResultResponseDto continueChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        challengeHistoryService.continueChallenge(challengeId, userDetails.getUser());
        return new ResultResponseDto("success", "챌린지를 계속 참여하실 수 있습니다.");
    }

    /**
     * 1주차 미션 성공 후 챌린지 그만두기 선택 API
     */
    @DeleteMapping("/continue")
    public ResultResponseDto stopChallenge(
            @PathVariable Long challengeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        challengeHistoryService.stopChallenge(challengeId, userDetails.getUser());
        return new ResultResponseDto("success", "챌린지 그만두기가 완료되었습니다.");
    }
}
