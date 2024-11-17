package POT.DuoBloom.domain.community.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.community.dto.response.CommunityListResponseDto;
import POT.DuoBloom.domain.community.dto.response.TagResponseDto;
import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityScrap;
import POT.DuoBloom.domain.community.repository.CommunityCommentRepository;
import POT.DuoBloom.domain.community.repository.CommunityLikeRepository;
import POT.DuoBloom.domain.community.repository.CommunityRepository;
import POT.DuoBloom.domain.community.repository.CommunityScrapRepository;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityScrapService {

    private final CommunityScrapRepository scrapRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Transactional
    public void scrapCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMUNITY_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!scrapRepository.existsByCommunity_CommunityIdAndUser_UserId(communityId, userId)) {
            scrapRepository.save(new CommunityScrap(user, community));
        }
    }

    @Transactional
    public void unscrapCommunity(Long communityId, Long userId) {
        CommunityScrap scrap = scrapRepository.findByCommunity_CommunityIdAndUser_UserId(communityId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));

        scrapRepository.delete(scrap);
    }

    @Transactional
    public List<CommunityListResponseDto> getScrappedCommunities(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return scrapRepository.findByUser_UserId(userId).stream()
                .map(scrap -> {
                    Community community = scrap.getCommunity();

                    // 좋아요 수 계산
                    long likeCount = communityLikeRepository.countByCommunity(community);

                    // 댓글 수 계산
                    long commentCount = communityCommentRepository.countByCommunity(community);

                    // 유저가 해당 커뮤니티를 좋아요 했는지 확인
                    boolean likedByUser = communityLikeRepository.findByUserAndCommunity(currentUser, community)
                            .isPresent();

                    // 이미지 URL 리스트 생성
                    List<String> imageUrls = community.getImageMappings().stream()
                            .map(mapping -> mapping.getCommunityImage().getImageUrl())
                            .collect(Collectors.toList());

                    // 태그 리스트 생성
                    List<TagResponseDto> tags = community.getTags().stream()
                            .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                            .collect(Collectors.toList());

                    // 작성자인지 확인
                    boolean isOwner = community.getUser().getUserId().equals(userId);

                    return new CommunityListResponseDto(
                            community.getCommunityId(),
                            community.getContent(),
                            community.getType(),
                            community.getUser().getNickname(),
                            community.getUser().getProfilePictureUrl(),
                            isOwner, // 작성자인지 여부
                            likedByUser, // 유저가 좋아요를 눌렀는지 여부
                            true, // 스크랩 여부
                            community.getCreatedAt(),
                            community.getUpdatedAt(),
                            imageUrls,
                            likeCount, // 좋아요 수
                            commentCount, // 댓글 수
                            tags // 태그 리스트
                    );
                }).collect(Collectors.toList());
    }


}
