package shop.fevertime.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.fevertime.backend.domain.*;
import shop.fevertime.backend.dto.request.ChallengeRequestDto;
import shop.fevertime.backend.dto.request.ChallengeUpdateRequestDto;
import shop.fevertime.backend.dto.response.ChallengeResponseDto;
import shop.fevertime.backend.dto.response.ChallengeResponseWithTotalCountDto;
import shop.fevertime.backend.dto.response.ResultResponseDto;
import shop.fevertime.backend.exception.ApiRequestException;
import shop.fevertime.backend.repository.CategoryRepository;
import shop.fevertime.backend.repository.CertificationRepository;
import shop.fevertime.backend.repository.ChallengeHistoryRepository;
import shop.fevertime.backend.repository.ChallengeRepository;
import shop.fevertime.backend.util.LocalDateTimeUtil;
import shop.fevertime.backend.util.S3Uploader;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final CategoryRepository categoryRepository;
    private final CertificationRepository certificationRepository;
    private final ChallengeHistoryRepository challengeHistoryRepository;
    private final S3Uploader s3Uploader;
    private final ChallengeHistoryService challengeHistoryService;

    public ChallengeResponseWithTotalCountDto getChallenges(String category, int page, String sortBy) {
        List<ChallengeResponseDto> challengeResponseDtoList = new ArrayList<>();
        ChallengeResponseWithTotalCountDto challengeResponseWithTotalCountDto;
        Page<Challenge> getChallenges;

        PageRequest pageRequest = PageRequest.of(page - 1, 9, Sort.by(Sort.Direction.DESC, "startDate"));

        if (Objects.equals(category, "All")) {
            if (Objects.equals(sortBy, "inProgress")) {
                getChallenges = challengeRepository.findAllByChallengeProgress(ChallengeProgress.INPROGRESS, pageRequest);
            } else if (Objects.equals(sortBy, "createdAt")) {
                getChallenges = challengeRepository.findAll(pageRequest);
            } else {
                throw new ApiRequestException("????????? ?????? ???????????????.");
            }
        } else {
            if (Objects.equals(sortBy, "inProgress")) {
                getChallenges = challengeRepository.findAllByChallengeProgressAndCategoryNameEquals(ChallengeProgress.INPROGRESS, category, pageRequest);
            } else if (Objects.equals(sortBy, "createdAt")) {
                getChallenges = challengeRepository.findAllByCategoryNameEquals(category, pageRequest);
            } else {
                throw new ApiRequestException("????????? ?????? ???????????????.");
            }
        }
        getChallengesWithParticipants(challengeResponseDtoList, getChallenges.getContent());
        challengeResponseWithTotalCountDto = new ChallengeResponseWithTotalCountDto(challengeResponseDtoList, getChallenges.getTotalElements());
        return challengeResponseWithTotalCountDto;
    }


    public ChallengeResponseDto getChallenge(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId).orElseThrow(() -> new ApiRequestException("?????? ???????????? ???????????? ????????????."));
        // ????????? ????????? ???
        long participants = challengeHistoryRepository.countDistinctUserByChallengeAndChallengeStatus(challenge, ChallengeStatus.JOIN);
        return new ChallengeResponseDto(challenge, participants);
    }

    public ChallengeResponseWithTotalCountDto searchChallenges(String search, int page) {
        List<ChallengeResponseDto> challengeResponseDtoList = new ArrayList<>();

        PageRequest pageRequest = PageRequest.of(page - 1, 9, Sort.by(Sort.Direction.ASC, "startDate"));

        Page<Challenge> getChallenges = challengeRepository.findAllByTitleContaining(search,pageRequest);

        ChallengeResponseWithTotalCountDto challengeList = new ChallengeResponseWithTotalCountDto(challengeResponseDtoList, getChallenges.getTotalElements());

        getChallengesWithParticipants(challengeResponseDtoList, getChallenges.getContent());

        return challengeList;
    }

    /**
     * ChallengeService method
     *
     * @param challengeResponseDtoList - ?????? ????????? ?????????
     * @param getChallenges            - challengeRepository ?????? ????????? Challege ?????????
     */
    private void getChallengesWithParticipants(List<ChallengeResponseDto> challengeResponseDtoList, List<Challenge> getChallenges) {
        for (Challenge getChallenge : getChallenges) {
            long participants = challengeHistoryRepository.countDistinctUserByChallengeAndChallengeStatus(getChallenge, ChallengeStatus.JOIN);
            ChallengeResponseDto challengeResponseDto = new ChallengeResponseDto(getChallenge, participants);
            challengeResponseDtoList.add(challengeResponseDto);
        }
    }

    @Transactional
    public void createChallenge(ChallengeRequestDto requestDto, User user, MultipartFile image) throws IOException {
        String uploadImageUrl;
        // ????????? ?????? ??? ?????? ??? ???????????????
        if (image == null) {
            uploadImageUrl = "https://samseburn-bucket.s3.ap-northeast-2.amazonaws.com/challenge/challenge.jfif";
        } else {
            // ????????? AWS S3 ?????????
            uploadImageUrl = s3Uploader.upload(image, "challenge");
        }

        // ???????????? ??????
        Category category = categoryRepository.findByName(requestDto.getCategory()).orElseThrow(() -> new ApiRequestException("???????????? ?????? ?????? ??????"));
        // ????????? ??????
        Challenge challenge = new Challenge(requestDto.getTitle(), requestDto.getDescription(), uploadImageUrl, LocalDateTimeUtil.getLocalDateTime(requestDto.getChallengeStartDate()), LocalDateTimeUtil.getLocalDateTime(requestDto.getChallengeEndDate()), requestDto.getLimitPerson(), requestDto.getLocationType(), requestDto.getAddress(), user, category, ChallengeProgress.INPROGRESS);
        challengeRepository.save(challenge);
        //????????? ????????? ????????? ???????????? ????????? ?????? ????????? ??????
        challengeHistoryService.joinChallenge(challenge.getId(), user);

    }

    @Transactional
    public void updateChallenge(Long challengeId, ChallengeUpdateRequestDto requestDto, User user, MultipartFile image) throws IOException {
        // ????????? ????????? s3?????? ?????? ????????? ??????
        Challenge challenge = challengeRepository.findByIdAndUser(challengeId, user).orElseThrow(
                () -> new ApiRequestException("?????? ???????????? ???????????? ????????????.")
        );

        //???????????? null?????? ????????? ????????????
        if (image == null) {
            challenge.updateAddress(requestDto.getAddress());
            return;
        }

        // ?????? ????????? S3?????? ?????? (?????? ????????? ?????? ????????? )
        if (!Objects.equals(challenge.getImgUrl(), "https://samseburn-bucket.s3.ap-northeast-2.amazonaws.com/challenge/challenge.jfif")) {
            String[] ar = challenge.getImgUrl().split("/");
            s3Uploader.delete(ar[ar.length - 1], "challenge");
        }

        // ????????? AWS S3 ?????????
        String uploadImageUrl = s3Uploader.upload(image, "challenge");

        // ?????? ???????????? ?????? ?????? ?????? -> ?????? ??????
        challenge.update(uploadImageUrl, requestDto.getAddress());
    }

    @Transactional
    public void deleteChallenge(Long challengeId, User user) {

        Challenge challenge = challengeRepository.findByIdAndUser(challengeId, user).orElseThrow(
                () -> new ApiRequestException("?????? ???????????? ???????????? ????????????.")
        );

        //???????????? ????????? ?????? ??????
        List<ChallengeHistory> all = challengeHistoryRepository.findAllByChallengeAndChallengeStatusAndUserNot(challenge, ChallengeStatus.JOIN, user);

        //?????? ?????? ???????????? ???????????? ????????? ??????
        if (all.size() == 0) {
            //???????????? ??????
            challengeHistoryRepository.deleteAllByChallenge(challenge);

            // ????????? ????????? s3?????? ??????
            String[] ar = challenge.getImgUrl().split("/");
            s3Uploader.delete(ar[ar.length - 1], "challenge");

            // ???????????? ???????????? ???????????? ?????? ????????? s3 ??????
            List<Certification> certifications = certificationRepository.findAllByChallenge(challenge);
            for (Certification certification : certifications) {
                String[] arr = certification.getImgUrl().split("/");
                s3Uploader.delete(arr[arr.length - 1], "certification");
            }
            certificationRepository.deleteAllByChallenge(challenge);
            challengeRepository.delete(challenge);
        } else {
            throw new ApiRequestException("???????????? ????????? ??? ????????????.");
        }

    }

    public ResultResponseDto checkChallengeCreator(Long challengeId, User user) {
        boolean present = challengeRepository.findByIdAndUser(challengeId, user).isPresent();
        if (present) {
            return new ResultResponseDto("success", "????????? ???????????? ????????????.");
        }
        return new ResultResponseDto("fail", "????????? ???????????? ????????????.");
    }
}
