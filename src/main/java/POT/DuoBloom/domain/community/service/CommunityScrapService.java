package POT.DuoBloom.domain.community.service;

import POT.DuoBloom.domain.community.dto.response.CommunityListResponseDto;
import POT.DuoBloom.domain.community.dto.response.TagResponseDto;
import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityScrap;
import POT.DuoBloom.domain.community.repository.CommunityLikeRepository;
import POT.DuoBloom.domain.community.repository.CommunityScrapRepository;
import POT.DuoBloom.domain.community.repository.CommunityRepository;
import POT.DuoBloom.domain.community.repository.CommunityCommentRepository;
import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityScrapService {

    private final CommunityScrapRepository communityScrapRepository;
    private final CommunityRepository communityRepository;
    private final CommunityLikeRepository likeRepository;
    private final CommunityCommentRepository commentRepository;

    @Transactional
    public void scrapCommunity(User user, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMUNITY_NOT_FOUND));
        if (communityScrapRepository.findByUserAndCommunity(user, community).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_SCRAPPED);
        }
        communityScrapRepository.save(new CommunityScrap(user, community));
    }

    @Transactional(readOnly = true)
    public List<CommunityListResponseDto> getScrappedCommunities(User user) {
        return communityScrapRepository.findByUser(user).stream()
                .map(scrap -> {
                    Community community = scrap.getCommunity();

                    long likeCount = likeRepository.countByCommunity(community);
                    boolean likedByUser = likeRepository.findByUserAndCommunity(user, community).isPresent();

                    long commentCount = commentRepository.countByCommunity(community);

                    List<TagResponseDto> tags = community.getTags().stream()
                            .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                            .collect(Collectors.toList());

                    List<String> imageUrls = community.getImageMappings().stream()
                            .map(mapping -> mapping.getCommunityImage().getImageUrl())
                            .collect(Collectors.toList());

                    return new CommunityListResponseDto(
                            community.getCommunityId(),
                            community.getContent(),
                            community.getType(),
                            community.getUser().getNickname(),
                            community.getUser().getProfilePictureUrl(),
                            true, // 스크랩한 게시글은 항상 내 것
                            likedByUser,
                            community.getCreatedAt(),
                            community.getUpdatedAt(),
                            imageUrls,
                            likeCount,
                            commentCount,
                            tags
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void unsaveCommunity(User user, Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMUNITY_NOT_FOUND));
        CommunityScrap scrap = communityScrapRepository.findByUserAndCommunity(user, community)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
        communityScrapRepository.delete(scrap);
    }
}
