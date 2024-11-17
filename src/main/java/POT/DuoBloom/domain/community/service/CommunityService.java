package POT.DuoBloom.domain.community.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.community.dto.request.CommunityRequestDto;
import POT.DuoBloom.domain.community.dto.response.*;
import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityLike;
import POT.DuoBloom.domain.community.entity.Tag;
import POT.DuoBloom.domain.community.entity.Type;
import POT.DuoBloom.domain.community.repository.*;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityScrapRepository communityScrapRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Transactional
    public void createCommunity(CommunityRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Community community = new Community(requestDto.getContent(), requestDto.getType(), user);

        if (requestDto.getTags() != null) {
            for (String tagName : requestDto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                community.addTag(tag);
            }
        }

        communityRepository.save(community);
    }

    @Transactional
    public void updateCommunity(Long communityId, CommunityRequestDto requestDto, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMUNITY_NOT_FOUND));

        if (!community.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        community.updateContent(requestDto.getContent());
        community.updateType(requestDto.getType());
        communityRepository.save(community);
    }

    @Transactional
    public void deleteCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMUNITY_NOT_FOUND));

        if (!community.getUser().getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        communityRepository.deleteById(communityId);
    }

    @Transactional(readOnly = true)
    public List<CommunityListResponseDto> getCommunityList(Long userId) {
        List<Community> communities = communityRepository.findAll();

        return communities.stream().map(community -> {
            long likeCount = communityLikeRepository.countByCommunity(community);
            long commentCount = communityCommentRepository.countByCommunity(community);

            List<TagResponseDto> tags = community.getTags().stream()
                    .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                    .collect(Collectors.toList());

            boolean likedByUser = communityLikeRepository.findByUserAndCommunity(
                    userRepository.findById(userId).orElse(null),
                    community
            ).isPresent();

            boolean scraped = communityScrapRepository.existsByCommunity_CommunityIdAndUser_UserId(
                    community.getCommunityId(), userId);

            return new CommunityListResponseDto(
                    community.getCommunityId(),
                    community.getContent(),
                    community.getType(),
                    community.getUser().getNickname(),
                    community.getUser().getProfilePictureUrl(),
                    community.getUser().getUserId().equals(userId),
                    likedByUser,
                    scraped, // 스크랩 여부 추가
                    community.getCreatedAt(),
                    community.getUpdatedAt(),
                    community.getImageMappings().stream()
                            .map(mapping -> mapping.getCommunityImage().getImageUrl())
                            .collect(Collectors.toList()),
                    likeCount,
                    commentCount,
                    tags
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityListResponseDto> getCommunitiesByType(String type, Long userId) {
        List<Community> communities = communityRepository.findByType(Type.valueOf(type.toUpperCase()));

        return communities.stream().map(community -> {
            long likeCount = communityLikeRepository.countByCommunity(community);
            long commentCount = communityCommentRepository.countByCommunity(community);

            List<TagResponseDto> tags = community.getTags().stream()
                    .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                    .collect(Collectors.toList());

            boolean likedByUser = communityLikeRepository.findByUserAndCommunity(
                    userRepository.findById(userId).orElse(null),
                    community
            ).isPresent();

            boolean scraped = communityScrapRepository.existsByCommunity_CommunityIdAndUser_UserId(
                    community.getCommunityId(), userId);

            return new CommunityListResponseDto(
                    community.getCommunityId(),
                    community.getContent(),
                    community.getType(),
                    community.getUser().getNickname(),
                    community.getUser().getProfilePictureUrl(),
                    community.getUser().getUserId().equals(userId),
                    likedByUser,
                    scraped, // 스크랩 여부 추가
                    community.getCreatedAt(),
                    community.getUpdatedAt(),
                    community.getImageMappings().stream()
                            .map(mapping -> mapping.getCommunityImage().getImageUrl())
                            .collect(Collectors.toList()),
                    likeCount,
                    commentCount,
                    tags
            );
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommunityListResponseDto> getTopCommunitiesByType(Long userId) {
        // 모든 타입 가져오기
        List<Type> allTypes = List.of(Type.values());

        // 각 타입별로 상위 2개 조회
        Pageable pageable = PageRequest.of(0, 2);
        List<CommunityListResponseDto> result = new ArrayList<>();

        for (Type type : allTypes) {
            List<Community> communities = communityRepository.findTopByType(type, pageable);
            result.addAll(communities.stream().map(community -> {
                // 좋아요 수 계산
                long likeCount = communityLikeRepository.countByCommunity(community);

                // 댓글 수 계산
                long commentCount = communityCommentRepository.countByCommunity(community);

                // 이미지 URL 리스트 생성
                List<String> imageUrls = community.getImageMappings().stream()
                        .map(mapping -> mapping.getCommunityImage().getImageUrl())
                        .collect(Collectors.toList());

                // 소유 여부 확인
                boolean isOwner = userId != null && userId.equals(community.getUser().getUserId());

                // 좋아요 여부 확인
                boolean likedByUser = userId != null && communityLikeRepository.findByUserAndCommunity(
                        userRepository.findById(userId).orElse(null), community).isPresent();

                // 스크랩 여부 확인
                boolean isScraped = userId != null && communityScrapRepository.existsByCommunity_CommunityIdAndUser_UserId(
                        community.getCommunityId(), userId);

                // 태그 리스트 생성
                List<TagResponseDto> tags = community.getTags().stream()
                        .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                        .collect(Collectors.toList());

                return new CommunityListResponseDto(
                        community.getCommunityId(),
                        community.getContent(),
                        community.getType(),
                        community.getUser().getNickname(),
                        community.getUser().getProfilePictureUrl(),
                        isOwner,
                        likedByUser,
                        isScraped, // 스크랩 여부 추가
                        community.getCreatedAt(),
                        community.getUpdatedAt(),
                        imageUrls,
                        likeCount,
                        commentCount,
                        tags
                );
            }).toList());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public CommunityFullResponseDto getCommunityWithDetails(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMUNITY_NOT_FOUND));

        boolean isLikedByUser = communityLikeRepository.findByUserAndCommunity(
                userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)),
                community
        ).isPresent();

        boolean isScraped = communityScrapRepository.existsByCommunity_CommunityIdAndUser_UserId(communityId, userId);

        CommunityResponseDto communityDto = new CommunityResponseDto(
                community.getCommunityId(),
                community.getContent(),
                community.getType(),
                community.getUser().getNickname(),
                community.getUser().getProfilePictureUrl(),
                isScraped // 스크랩 여부 추가
        );

        List<CommunityImageResponseDto> images = community.getImageMappings()
                .stream()
                .map(mapping -> new CommunityImageResponseDto(
                        mapping.getCommunityImage().getImageId(),
                        mapping.getCommunityImage().getImageUrl()))
                .collect(Collectors.toList());

        List<CommunityCommentResponseDto> comments = communityCommentRepository.findByCommunity(community)
                .stream()
                .map(comment -> new CommunityCommentResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfilePictureUrl(),
                        comment.getUser().getUserId().equals(userId)))
                .collect(Collectors.toList());

        long likeCount = communityLikeRepository.countByCommunity(community);

        List<TagResponseDto> tags = community.getTags().stream()
                .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                .collect(Collectors.toList());

        boolean isOwner = community.getUser().getUserId().equals(userId);

        return new CommunityFullResponseDto(
                communityDto,
                images,
                comments,
                likeCount,
                isLikedByUser, // 중복 선언 제거 후 사용
                isOwner,
                tags
        );
    }

    @Transactional
    public void toggleLike(Long communityId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        communityLikeRepository.findByUserAndCommunity(user, community).ifPresentOrElse(
                communityLikeRepository::delete,
                () -> communityLikeRepository.save(new CommunityLike(user, community))
        );
    }


}
