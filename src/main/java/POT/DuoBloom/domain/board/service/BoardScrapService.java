package POT.DuoBloom.domain.board.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.board.dto.response.BoardListDto;
import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardScrap;
import POT.DuoBloom.domain.board.repository.BoardCommentRepository;
import POT.DuoBloom.domain.board.repository.BoardLikeRepository;
import POT.DuoBloom.domain.board.repository.BoardRepository;
import POT.DuoBloom.domain.board.repository.BoardScrapRepository;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardScrapService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository likeRepository;
    private final BoardCommentRepository commentRepository;
    private final BoardScrapRepository boardScrapRepository;
    private final UserRepository userRepository;

    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            return Long.valueOf(((UserDetails) principal).getUsername()); // username에 userId가 저장된 경우
        }

        throw new CustomException(ErrorCode.UNAUTHORIZED);
    }

    /**
     * 게시글 스크랩
     */
    @Transactional
    public void scrapBoard(Integer boardId, Long userId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!boardScrapRepository.existsByBoard_BoardIdAndUser_UserId(boardId, userId)) {
            boardScrapRepository.save(new BoardScrap(board, user));
        }
    }

    /**
     * 게시글 스크랩 취소
     */
    @Transactional
    public void unscrapBoard(Integer boardId, Long userId) {
        BoardScrap scrap = boardScrapRepository.findByBoard_BoardIdAndUser_UserId(boardId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
        boardScrapRepository.delete(scrap);
    }

    /**
     * 스크랩된 게시글 목록 조회
     */
    @Transactional
    public List<BoardListDto> getScrappedBoards(Long userId) {
        List<BoardScrap> scraps = boardScrapRepository.findByUser_UserId(userId);

        return scraps.stream()
                .map(scrap -> convertToDto(scrap.getBoard(), userId))
                .collect(Collectors.toList());
    }

    /**
     * Board 엔티티를 BoardListDto로 변환
     */
    private BoardListDto convertToDto(Board board, Long userId) {
        boolean likedByUser = likeRepository.existsByUser_UserIdAndBoard_BoardId(userId, board.getBoardId());
        int likeCount = likeRepository.countByBoard(board);
        int commentCount = commentRepository.countByBoard(board);

        return new BoardListDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                likeCount, // 좋아요 수
                commentCount, // 댓글 수
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                board.getUser().getUserId().equals(userId), // isMine
                likedByUser,
                true // 스크랩 여부
        );
    }
}

