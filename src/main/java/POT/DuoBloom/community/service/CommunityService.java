package POT.DuoBloom.community.service;

import POT.DuoBloom.community.dto.CommunityRequestDto;
import POT.DuoBloom.community.dto.CommunityResponseDto;
import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.repository.CommunityRepository;
import POT.DuoBloom.user.UserRepository;
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
        return toResponseDto(savedCommunity, true);  // 생성한 사용자 자신이므로 isOwner를 true로 설정
    }

    @Transactional(readOnly = true)
    public List<CommunityResponseDto> getAllCommunities(Long userId) {
        return communityRepository.findAll()
                .stream()
                .map(community -> toResponseDto(community, community.getUser().getUserId().equals(userId)))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommunityResponseDto updateCommunity(Integer communityId, CommunityRequestDto requestDto, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        if (!community.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to update this post");
        }

        community.updateContent(requestDto.getContent());
        community.updateType(requestDto.getType());

        Community updatedCommunity = communityRepository.save(community);
        return toResponseDto(updatedCommunity, true);
    }

    @Transactional
    public void deleteCommunity(Integer communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        if (!community.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this post");
        }

        communityRepository.deleteById(communityId);
    }

    private CommunityResponseDto toResponseDto(Community community, boolean isOwner) {
        return new CommunityResponseDto(
                community.getCommunityId(),
                community.getContent(),
                community.getType(),
                community.getCreatedAt(),
                community.getUpdatedAt(),
                isOwner
        );
    }
}
