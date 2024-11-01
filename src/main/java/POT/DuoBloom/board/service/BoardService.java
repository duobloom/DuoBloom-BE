package POT.DuoBloom.board.service;

import POT.DuoBloom.board.dto.BoardCommentDto;
import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardComment;
import POT.DuoBloom.board.entity.BoardLike;
import POT.DuoBloom.board.repository.BoardCommentRepository;
import POT.DuoBloom.board.repository.BoardRepository;
import POT.DuoBloom.board.repository.BoardLikeRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardLikeRepository likeRepository;
    private final BoardCommentRepository boardCommentRepository;

    // 커플이 연결된 경우에만 접근 가능
    public boolean canAccessBoard(User user) {
        return user.getCoupleUser() != null;
    }

    // 글 작성
    @Transactional
    public Board createBoard(User user, String title, String content) {
        if (!canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티에 글을 작성할 수 있습니다.");
        }
        Board board = new Board(user, title, content, LocalDateTime.now());
        return boardRepository.save(board);
    }

    // 전체 글 조회 (좋아요 수 포함)
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(board -> {
                    int likeCount = likeRepository.countByBoard(board);
                    return new BoardResponseDto(
                            board.getBoardId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getUpdatedAt(),
                            null, // 댓글은 제외
                            likeCount
                    );
                })
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회 (좋아요 수 및 댓글 포함)
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardDetailsById(Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalStateException("해당 글이 존재하지 않습니다."));
        int likeCount = likeRepository.countByBoard(board);

        List<BoardComment> comments = boardCommentRepository.findByBoard_BoardId(boardId);
        List<BoardCommentDto> commentDtos = comments.stream()
                .map(comment -> new BoardCommentDto(comment.getId(), comment.getUser().getNickname(),
                        comment.getContent(), comment.getCreatedAt()))
                .collect(Collectors.toList());

        return new BoardResponseDto(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                board.getUpdatedAt(),
                commentDtos,
                likeCount
        );
    }

    // 날짜별 사용자 게시글 조회 (좋아요 수 포함)
    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoardsByDateAndUser(LocalDate date, User user) {
        List<Board> boards = boardRepository.findByUserAndFeedDate(user, date);
        return boards.stream()
                .map(board -> {
                    int likeCount = likeRepository.countByBoard(board);
                    return new BoardResponseDto(
                            board.getBoardId(),
                            board.getTitle(),
                            board.getContent(),
                            board.getUpdatedAt(),
                            null, // 댓글 제외
                            likeCount
                    );
                })
                .collect(Collectors.toList());
    }

    // 글 수정
    @Transactional
    public Board updateBoard(User user, Integer boardId, String title, String content) {
        if (!canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티 글을 수정할 수 있습니다.");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if (!board.getUser().equals(user)) {
            throw new IllegalStateException("해당 글의 작성자만 수정할 수 있습니다.");
        }

        board.updateTitle(title);
        board.updateContent(content);
        board.updateUpdatedAt(LocalDateTime.now());
        return boardRepository.save(board);
    }

    // 글 삭제
    @Transactional
    public void deleteBoard(User user, Integer boardId) {
        if (!canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티 글을 삭제할 수 있습니다.");
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if (!board.getUser().equals(user)) {
            throw new IllegalStateException("해당 글의 작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }

    // 좋아요 추가
    @Transactional
    public void likeBoard(User user, Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if (likeRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시물입니다.");
        }

        likeRepository.save(new BoardLike(user, board));
    }

    // 좋아요 취소
    @Transactional
    public void unlikeBoard(User user, Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        if (!likeRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalStateException("좋아요를 누르지 않은 게시물입니다.");
        }

        likeRepository.deleteByUserAndBoard(user, board);
    }

    // 댓글 추가
    @Transactional
    public BoardComment addComment(User user, Integer boardId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        BoardComment boardComment = new BoardComment(user, board, content);
        return boardCommentRepository.save(boardComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        BoardComment boardComment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        boardCommentRepository.delete(boardComment);
    }
}
