package POT.DuoBloom.domain.board.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.board.dto.request.BoardRequestDto;
import POT.DuoBloom.domain.board.dto.response.BoardCommentDto;
import POT.DuoBloom.domain.board.dto.response.BoardListDto;
import POT.DuoBloom.domain.board.dto.response.BoardResponseDto;
import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardComment;
import POT.DuoBloom.domain.board.entity.BoardLike;
import POT.DuoBloom.domain.board.repository.BoardCommentRepository;
import POT.DuoBloom.domain.board.repository.BoardLikeRepository;
import POT.DuoBloom.domain.board.repository.BoardRepository;
import POT.DuoBloom.domain.board.repository.BoardScrapRepository;
import POT.DuoBloom.domain.user.entity.User;
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
    private final BoardCommentRepository commentRepository;
    private final BoardScrapRepository scrapRepository;

    // 커플 연결 여부 확인
    private boolean canAccessBoard(User user) {
        return user.getCoupleUser() != null;
    }

    @Transactional
    public void createBoard(User user, String content, List<String> photoUrls) {
        if (!canAccessBoard(user)) {
            throw new CustomException(ErrorCode.COUPLE_ONLY_ACCESS);
        }

        Board board = new Board(user, content, LocalDateTime.now());

        if (photoUrls != null) {
            photoUrls.forEach(board::addPhotoUrl);
        }

        boardRepository.save(board);

    }



    @Transactional(readOnly = true)
    public List<BoardListDto> getAllBoards(User currentUser) {
        Long currentUserId = currentUser.getUserId();

        return boardRepository.findAll().stream()
                .map(board -> {
                    boolean likedByUser = likeRepository.existsByUserAndBoard(currentUser, board);
                    boolean scraped = scrapRepository.existsByBoard_BoardIdAndUser_UserId(board.getBoardId(), currentUserId);
                    return convertToListDto(board, currentUserId, likedByUser, scraped);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BoardResponseDto getBoardDetailsById(Integer boardId, User currentUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        List<BoardComment> comments = commentRepository.findByBoard_BoardId(boardId);
        List<BoardCommentDto> commentDtos = comments.stream()
                .map(comment -> convertToCommentDto(comment, currentUser.getUserId()))
                .collect(Collectors.toList());

        boolean likedByUser = likeRepository.existsByUserAndBoard(currentUser, board);
        boolean isMine = board.getUser().getUserId().equals(currentUser.getUserId());

        return convertToResponseDtoWithComments(board, currentUser.getUserId(), likedByUser, isMine, commentDtos);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoardsByDateAndUser(LocalDate date, User targetUser, Long currentUserId) {
        List<Board> boards = boardRepository.findByUserAndFeedDate(targetUser, date);

        return boards.stream()
                .map(board -> {
                    // 현재 사용자가 게시글을 좋아요 눌렀는지 확인
                    boolean likedByUser = likeRepository.existsByUserAndBoard(targetUser, board);

                    // 현재 사용자가 게시글을 스크랩했는지 확인
                    boolean scraped = scrapRepository.existsByBoard_BoardIdAndUser_UserId(board.getBoardId(), currentUserId);

                    // ResponseDto 변환
                    return convertToResponseDto(board, currentUserId, likedByUser, scraped);
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public void updateBoard(User user, Integer boardId, BoardRequestDto boardRequestDto) {
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

        boardRepository.save(board); // 변경 내용을 저장
    }


    @Transactional
    public void deleteBoard(User user, Integer boardId) {
        if (!canAccessBoard(user)) {
            throw new CustomException(ErrorCode.COUPLE_ONLY_ACCESS);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        if (!board.getUser().equals(user)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }

        boardRepository.delete(board);
    }

    private BoardResponseDto convertToResponseDto(Board board, Long userId, boolean likedByUser, boolean scraped) {
        return new BoardResponseDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                null, // 댓글 정보는 여기서 처리하지 않음
                likeRepository.countByBoard(board),
                commentRepository.countByBoard_BoardId(board.getBoardId()),
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                board.getUser().getUserId().equals(userId),
                likedByUser,
                scraped
        );
    }


    private BoardResponseDto convertToResponseDtoWithComments(Board board, Long userId, boolean likedByUser, boolean isMine, List<BoardCommentDto> comments) {
        boolean scraped = scrapRepository.existsByBoard_BoardIdAndUser_UserId(board.getBoardId(), userId);

        return new BoardResponseDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                comments,
                likeRepository.countByBoard(board),
                commentRepository.countByBoard_BoardId(board.getBoardId()),
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                isMine,
                likedByUser,
                scraped
        );
    }

    private BoardCommentDto convertToCommentDto(BoardComment comment, Long userId) {
        return new BoardCommentDto(
                comment.getId(),
                comment.getUser().getNickname(),
                comment.getUser().getProfilePictureUrl(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUser().getUserId().equals(userId)
        );
    }

    private BoardListDto convertToListDto(Board board, Long userId, boolean likedByUser, boolean scraped) {
        return new BoardListDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                likeRepository.countByBoard(board),
                commentRepository.countByBoard_BoardId(board.getBoardId()),
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                board.getUser().getUserId().equals(userId),
                likedByUser,
                scraped
        );
    }


    // 특정 사용자가 특정 게시물에 좋아요를 눌렀는지 확인
    @Transactional(readOnly = true)
    public boolean isBoardLikedByUser(User user, Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        return likeRepository.existsByUserAndBoard(user, board);
    }


    // 좋아요 추가
    @Transactional
    public void likeBoard(User user, Integer boardId) {
        if (isBoardLikedByUser(user, boardId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        likeRepository.save(new BoardLike(user, board));
    }

    // 좋아요 취소
    @Transactional
    public void unlikeBoard(User user, Integer boardId) {
        if (!isBoardLikedByUser(user, boardId)) {
            throw new CustomException(ErrorCode.NOT_LIKED);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        likeRepository.deleteByUserAndBoard(user, board);
    }

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
