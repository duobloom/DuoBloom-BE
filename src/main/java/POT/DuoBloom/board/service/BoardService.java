package POT.DuoBloom.board.service;

import POT.DuoBloom.board.dto.BoardCommentDto;
import POT.DuoBloom.board.dto.BoardRequestDto;
import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardComment;
import POT.DuoBloom.board.entity.BoardLike;
import POT.DuoBloom.board.repository.BoardCommentRepository;
import POT.DuoBloom.board.repository.BoardRepository;
import POT.DuoBloom.board.repository.BoardLikeRepository;
import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
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

    @Transactional
    public BoardResponseDto createBoard(User user, String content, List<String> photoUrls) {
        if (!canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티에 글을 작성할 수 있습니다.");
        }
        Board board = new Board(user, content, LocalDateTime.now());

        if (photoUrls != null) {
            photoUrls.forEach(board::addPhotoUrl);
        }

        boardRepository.save(board);

        // 반환할 때 BoardResponseDto로 변환
        return new BoardResponseDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                null, // 댓글 정보는 여기서 처리하지 않음
                likeRepository.countByBoard(board),
                boardCommentRepository.countByBoard_BoardId(board.getBoardId()),
                user.getNickname(),
                user.getProfilePictureUrl(),
                true, // 현재 사용자가 작성자이므로 true
                false
        );
    }


    @Transactional(readOnly = true)
    public List<BoardResponseDto> getAllBoards(User currentUser) {
        Long currentUserId = currentUser.getUserId();
        User coupleUser = currentUser.getCoupleUser();
        Long coupleUserId = (coupleUser != null) ? coupleUser.getUserId() : null;

        return boardRepository.findAll().stream()
                .filter(board -> board.getUser().getUserId().equals(currentUserId) ||
                        (coupleUserId != null && board.getUser().getUserId().equals(coupleUserId)))
                .map(board -> {
                    boolean likedByUser = likeRepository.existsByUserAndBoard(currentUser, board);
                    return new BoardResponseDto(
                            board.getBoardId(),
                            board.getContent(),
                            board.getUpdatedAt(),
                            board.getPhotoUrls(),
                            null, // 댓글 정보는 여기서 처리하지 않음
                            likeRepository.countByBoard(board),
                            boardCommentRepository.countByBoard_BoardId(board.getBoardId()),
                            board.getUser().getNickname(),
                            board.getUser().getProfilePictureUrl(),
                            board.getUser().getUserId().equals(currentUserId),
                            likedByUser // 추가된 필드 설정
                    );
                })
                .collect(Collectors.toList());
    }





    @Transactional(readOnly = true)
    public BoardResponseDto getBoardDetailsById(Integer boardId, User currentUser) {
        Long currentUserId = currentUser.getUserId();

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        boolean likedByUser = likeRepository.existsByUserAndBoard(currentUser, board);

        List<BoardComment> comments = boardCommentRepository.findByBoard_BoardId(boardId);
        List<BoardCommentDto> commentDtos = comments.stream()
                .map(comment -> new BoardCommentDto(
                        comment.getId(),
                        comment.getUser().getNickname(),
                        comment.getUser().getProfilePictureUrl(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUser().getUserId().equals(currentUserId)
                ))
                .collect(Collectors.toList());

        boolean isMine = board.getUser().getUserId().equals(currentUserId);

        return new BoardResponseDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                commentDtos,
                likeRepository.countByBoard(board),
                boardCommentRepository.countByBoard_BoardId(board.getBoardId()),
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                isMine,
                likedByUser
        );
    }





    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoardsByDateAndUser(LocalDate date, User targetUser, Long currentUserId) {
        List<Board> boards = boardRepository.findByUserAndFeedDate(targetUser, date);
        return boards.stream()
                .map(board -> {
                    // 현재 사용자가 게시글을 좋아요 눌렀는지 확인
                    boolean likedByUser = likeRepository.existsByUserAndBoard(
                            targetUser, // 현재 조회하는 사용자
                            board
                    );

                    return new BoardResponseDto(
                            board.getBoardId(),
                            board.getContent(),
                            board.getUpdatedAt(),
                            board.getPhotoUrls(),
                            null, // 댓글 정보는 여기서 처리하지 않음
                            likeRepository.countByBoard(board),
                            boardCommentRepository.countByBoard_BoardId(board.getBoardId()),
                            targetUser.getNickname(),
                            targetUser.getProfilePictureUrl(),
                            board.getUser().getUserId().equals(currentUserId),
                            likedByUser // likedByUser 추가
                    );
                })
                .collect(Collectors.toList());
    }





    // 글 수정
    @Transactional
    public BoardResponseDto updateBoard(User user, Integer boardId, BoardRequestDto boardRequestDto) {
        if (!canAccessBoard(user)) {
            throw new CustomException(ErrorCode.COUPLE_ONLY_ACCESS);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        board.updateContent(boardRequestDto.getContent());
        board.getPhotoUrls().clear();
        board.getPhotoUrls().addAll(boardRequestDto.getPhotoUrls());
        board.updateUpdatedAt(LocalDateTime.now());

        boolean likedByUser = likeRepository.existsByUserAndBoard(user, board);

        return new BoardResponseDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                null,
                likeRepository.countByBoard(board),
                boardCommentRepository.countByBoard_BoardId(board.getBoardId()),
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                true,
                likedByUser
        );
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
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (likeRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalStateException("이미 좋아요를 누른 게시물입니다.");
        }

        likeRepository.save(new BoardLike(user, board));
    }

    // 좋아요 취소
    @Transactional
    public void unlikeBoard(User user, Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!likeRepository.existsByUserAndBoard(user, board)) {
            throw new IllegalStateException("좋아요를 누르지 않은 게시물입니다.");
        }

        likeRepository.deleteByUserAndBoard(user, board);
    }

    // 댓글 추가
    @Transactional
    public BoardComment addComment(User user, Integer boardId, String content) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        BoardComment boardComment = new BoardComment(user, board, content);
        return boardCommentRepository.save(boardComment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        BoardComment boardComment = boardCommentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        boardCommentRepository.delete(boardComment);
    }
}