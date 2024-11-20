package POT.DuoBloom.domain.community.service;

import POT.DuoBloom.domain.community.dto.request.CommunityCommentRequestDto;
import POT.DuoBloom.domain.community.dto.response.CommunityCommentResponseDto;
import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityComment;
import POT.DuoBloom.domain.community.repository.CommunityCommentRepository;
import POT.DuoBloom.domain.community.repository.CommunityRepository;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommunityCommentResponseDto addComment(Long communityId, CommunityCommentRequestDto requestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        CommunityComment comment = new CommunityComment(requestDto.getContent(), user, community);
        CommunityComment savedComment = communityCommentRepository.save(comment);

        return new CommunityCommentResponseDto(
                savedComment.getCommentId(),
                savedComment.getContent(),
                user.getNickname(),
                user.getProfilePictureUrl(),
                true,
                savedComment.getCreatedAt(),
                savedComment.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommunityComment comment = communityCommentRepository.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this comment");
        }

        communityCommentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentResponseDto> getComments(Long communityId, Long userId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        return communityCommentRepository.findByCommunity(community)
                .stream()
                .map(comment -> new CommunityCommentResponseDto(
                        comment.getCommentId(),
                        comment.getContent(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfilePictureUrl(),
                        user != null && comment.getUser().getUserId().equals(userId),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

}
