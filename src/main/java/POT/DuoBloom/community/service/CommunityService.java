package POT.DuoBloom.community.service;

import POT.DuoBloom.community.dto.*;
import POT.DuoBloom.community.dto.CommunityResponseDto;
import POT.DuoBloom.community.entity.*;
import POT.DuoBloom.community.repository.*;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityLikeRepository communityLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Community community = new Community(requestDto.getContent(), requestDto.getType(), user);
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
        // 1. Community 기본 정보 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        // 2. CommunityResponseDto 생성
        CommunityResponseDto communityDto = new CommunityResponseDto(
                community.getCommunityId(),
                community.getContent(),
                community.getType(),
                community.getUser().getNickname(),
                community.getUser().getProfilePictureUrl()
        );

        // 3. CommunityImageResponseDto 리스트 생성
        List<CommunityImageResponseDto> images = community.getImageMappings()
                .stream()
                .map(mapping -> new CommunityImageResponseDto(
                        mapping.getCommunityImage().getImageId(),
                        mapping.getCommunityImage().getImageUrl()))
                .collect(Collectors.toList());

        // 4. CommunityCommentResponseDto 리스트 생성
        List<CommunityCommentResponseDto> comments = communityCommentRepository.findByCommunity(community)
                .stream()
                .map(comment -> new CommunityCommentResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfilePictureUrl(),
                        comment.getUser().getUserId().equals(userId)))
                .collect(Collectors.toList());

        // 5. 좋아요 수 및 상태 계산
        long likeCount = communityLikeRepository.countByCommunity(community);
        boolean isLikedByUser = communityLikeRepository.findByUserAndCommunity(
                userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")),
                community
        ).isPresent();

        // 6. 소유 여부 확인
        boolean isOwner = community.getUser().getUserId().equals(userId);

        // 7. CommunityFullResponseDto 생성 및 반환
        return new CommunityFullResponseDto(
                communityDto,
                images,
                comments,
                likeCount,
                isLikedByUser,
                isOwner
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
    public List<CommunityListResponseDto> getCommunityList(Long userId) {
        List<Community> communities = communityRepository.findAll();

        return communities.stream().map(community -> {
            long likeCount = communityLikeRepository.countByCommunity(community);
            long commentCount = communityCommentRepository.countByCommunity(community);

            // 이미지 URL 리스트 생성
            List<String> imageUrls = community.getImageMappings()
                    .stream()
                    .map(mapping -> mapping.getCommunityImage().getImageUrl())
                    .collect(Collectors.toList());

            // 소유 여부 확인
            boolean isOwner = community.getUser().getUserId().equals(userId);

            // 좋아요 여부 확인
            boolean likedByUser = communityLikeRepository.findByUserAndCommunity(
                    userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")),
                    community
            ).isPresent();

            return new CommunityListResponseDto(
                    community.getCommunityId(),
                    community.getContent(),
                    community.getType(),
                    community.getUser().getNickname(),
                    community.getUser().getProfilePictureUrl(),
                    isOwner,
                    likedByUser, // 추가
                    community.getCreatedAt(),
                    community.getUpdatedAt(),
                    imageUrls,
                    likeCount,
                    commentCount
            );
        }).collect(Collectors.toList());
    }

}
