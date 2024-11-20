package POT.DuoBloom.domain.board.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardComment;
import POT.DuoBloom.domain.board.repository.BoardCommentRepository;
import POT.DuoBloom.domain.board.repository.BoardLikeRepository;
import POT.DuoBloom.domain.board.repository.BoardRepository;
import POT.DuoBloom.domain.board.repository.BoardScrapRepository;
import POT.DuoBloom.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardCommentService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository likeRepository;
    private final BoardCommentRepository commentRepository;
    private final BoardScrapRepository scrapRepository;

    // 댓글 추가
    @Transactional
    public BoardComment addComment(User user, Integer boardId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        BoardComment boardComment = new BoardComment(user, board, content);
        return commentRepository.save(boardComment);
    }

    @Transactional
    public BoardComment updateComment(Long commentId, User user, String newContent) {
        BoardComment boardComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!boardComment.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        boardComment.updateContent(newContent);
        return commentRepository.save(boardComment);
    }


    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        BoardComment boardComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(boardComment);
    }

}
