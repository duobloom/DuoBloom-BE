package POT.DuoBloom.community.service;

import POT.DuoBloom.community.dto.request.CommunityRequestDto;
import POT.DuoBloom.community.dto.response.*;
import POT.DuoBloom.community.entity.*;
import POT.DuoBloom.community.repository.*;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final TagRepository tagRepository;

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Community community = new Community(requestDto.getContent(), requestDto.getType(), user);

        if (requestDto.getTags() != null) {
            for (String tagName : requestDto.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> tagRepository.save(new Tag(tagName)));
                community.addTag(tag);
            }
        }

        Community savedCommunity = communityRepository.save(community);
        return new CommunityResponseDto(
                savedCommunity.getCommunityId(),
                savedCommunity.getContent(),
                savedCommunity.getType(),
                savedCommunity.getUser().getNickname(),
                savedCommunity.getUser().getProfilePictureUrl()
        );
    }


    @Transactional(readOnly = true)
    public CommunityFullResponseDto getCommunityWithDetails(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        CommunityResponseDto communityDto = new CommunityResponseDto(
                community.getCommunityId(),
                community.getContent(),
                community.getType(),
                community.getUser().getNickname(),
                community.getUser().getProfilePictureUrl()
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
        boolean isLikedByUser = communityLikeRepository.findByUserAndCommunity(
                userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")),
                community
        ).isPresent();

        List<TagResponseDto> tags = community.getTags().stream()
                .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                .collect(Collectors.toList());

        boolean isOwner = community.getUser().getUserId().equals(userId);

        return new CommunityFullResponseDto(
                communityDto,
                images,
                comments,
                likeCount,
                isLikedByUser,
                isOwner,
                tags
        );
    }


    @Transactional
    public CommunityResponseDto updateCommunity(Long communityId, CommunityRequestDto requestDto, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        if (!community.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this post");
        }

        community.updateContent(requestDto.getContent());
        community.updateType(requestDto.getType());

        Community updatedCommunity = communityRepository.save(community);
        return new CommunityResponseDto(
                updatedCommunity.getCommunityId(),
                updatedCommunity.getContent(),
                updatedCommunity.getType(),
                updatedCommunity.getUser().getNickname(),
                updatedCommunity.getUser().getProfilePictureUrl()
        );
    }

    @Transactional
    public void deleteCommunity(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        if (!community.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this post");
        }

        communityRepository.deleteById(communityId);
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
                boolean likedByUser = communityLikeRepository.findByUserAndCommunity(
                        userRepository.findById(userId).orElse(null), community).isPresent();

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



    // 테스트용
    @Transactional(readOnly = true)
    public List<CommunityListResponseDto> getCommunityList(Long userId) {
        List<Community> communities = communityRepository.findAll();

        return communities.stream().map(community -> {
            long likeCount = communityLikeRepository.countByCommunity(community);
            long commentCount = communityCommentRepository.countByCommunity(community);

            List<TagResponseDto> tags = community.getTags().stream()
                    .map(tag -> new TagResponseDto(tag.getTagId(), tag.getName()))
                    .collect(Collectors.toList());

            boolean isLikedByUser = communityLikeRepository.findByUserAndCommunity(
                    userRepository.findById(userId).orElse(null),
                    community
            ).isPresent();

            return new CommunityListResponseDto(
                    community.getCommunityId(),
                    community.getContent(),
                    community.getType(),
                    community.getUser().getNickname(),
                    community.getUser().getProfilePictureUrl(),
                    community.getUser().getUserId().equals(userId),
                    isLikedByUser,
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


}
