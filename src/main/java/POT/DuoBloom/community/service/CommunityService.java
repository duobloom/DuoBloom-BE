package POT.DuoBloom.community.service;

import POT.DuoBloom.community.dto.CommunityRequestDto;
import POT.DuoBloom.community.dto.CommunityResponseDto;
import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.entity.CommunityLike;
import POT.DuoBloom.community.repository.CommunityLikeRepository;
import POT.DuoBloom.community.repository.CommunityRepository;
import POT.DuoBloom.user.repository.UserRepository;
import POT.DuoBloom.user.entity.User;
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
    private final CommunityLikeRepository communityLikeRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityResponseDto createCommunity(CommunityRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Community community = new Community(
                requestDto.getContent(),
                requestDto.getType(),
                user
        );

        Community savedCommunity = communityRepository.save(community);
        return toResponseDto(savedCommunity, true, 0, false);
    }


    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getAllCommunities(Long userId) {
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        return communityRepository.findAll()
                .stream()
                .map(community -> {
                    boolean isOwner = user != null && community.getUser().getUserId().equals(userId);
                    long likeCount = communityLikeRepository.countByCommunity(community);
                    boolean isLikedByUser = user != null && communityLikeRepository.findByUserAndCommunity(user, community).isPresent();
                    return toResponseDto(community, isOwner, likeCount, isLikedByUser);
                })
                .collect(Collectors.toList());
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

        long likeCount = communityLikeRepository.countByCommunity(updatedCommunity);
        boolean isLikedByUser = communityLikeRepository.findByUserAndCommunity(
                userRepository.findById(userId).orElse(null), updatedCommunity).isPresent();

        return toResponseDto(updatedCommunity, true, likeCount, isLikedByUser);
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
                communityLikeRepository::delete, // 이미 좋아요를 누른 경우 취소하도록
                () -> {
                    if (!communityLikeRepository.findByUserAndCommunity(user, community).isPresent()) {
                        communityLikeRepository.save(new CommunityLike(user, community));
                    }
                }
        );
    }

    private CommunityResponseDto toResponseDto(Community community, boolean isOwner, long likeCount, boolean isLikedByUser) {
        return new CommunityResponseDto(
                community.getCommunityId(),
                community.getContent(),
                community.getType(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                isOwner,
                likeCount,
                isLikedByUser
        );
    }
}
