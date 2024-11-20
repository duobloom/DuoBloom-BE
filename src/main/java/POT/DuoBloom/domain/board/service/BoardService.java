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

    @Transactional(readOnly = true)
    public List<BoardResponseDto> getBoardsByDateAndUser(LocalDate date, User targetUser, Long currentUserId) {
        // 특정 날짜와 사용자의 게시글 조회
        List<Board> boards = boardRepository.findByUserAndFeedDate(targetUser, date);

        return boards.stream()
                .map(board -> {
                    // 현재 사용자가 해당 게시글을 좋아요 눌렀는지 확인
                    boolean likedByUser = likeRepository.existsByUser_UserIdAndBoard_BoardId(currentUserId, board.getBoardId());

                    // 현재 사용자가 해당 게시글을 스크랩했는지 확인
                    boolean scraped = scrapRepository.existsByBoard_BoardIdAndUser_UserId(board.getBoardId(), currentUserId);

                    // ResponseDto로 변환
                    return convertToResponseDto(board, currentUserId, likedByUser, scraped);
                })
                .collect(Collectors.toList());
    }


    // 게시글 생성
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

    // 전체 게시글 조회
    @Transactional(readOnly = true)
    public List<BoardListDto> getAllBoards(User currentUser) {
        Long currentUserId = currentUser.getUserId();

        return boardRepository.findAll().stream()
                .map(board -> {
                    log.debug("Checking if user [{}] liked board [{}]", currentUserId, board.getBoardId());
                    boolean likedByUser = likeRepository.existsByUser_UserIdAndBoard_BoardId(currentUserId, board.getBoardId());
                    log.debug("Liked by user result: {}", likedByUser);

                    boolean scraped = scrapRepository.existsByBoard_BoardIdAndUser_UserId(board.getBoardId(), currentUserId);
                    return convertToListDto(board, currentUserId, likedByUser, scraped);
                })
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    @Transactional(readOnly = true)
    public BoardResponseDto getBoardDetailsById(Integer boardId, User currentUser) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));

        List<BoardComment> comments = commentRepository.findByBoard_BoardId(boardId);
        List<BoardCommentDto> commentDtos = comments.stream()
                .map(comment -> convertToCommentDto(comment, currentUser.getUserId()))
                .collect(Collectors.toList());

        boolean likedByUser = likeRepository.existsByUser_UserIdAndBoard_BoardId(currentUser.getUserId(), boardId);
        boolean isMine = board.getUser().getUserId().equals(currentUser.getUserId());

        return convertToResponseDtoWithComments(board, currentUser.getUserId(), likedByUser, isMine, commentDtos);
    }

    // 게시글 수정
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

    // 게시글 삭제
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

    // 좋아요 추가
    @Transactional
    public void likeBoard(User user, Integer boardId) {
        if (likeRepository.existsByUser_UserIdAndBoard_BoardId(user.getUserId(), boardId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        likeRepository.save(new BoardLike(user, board));
    }

    // 좋아요 취소
    @Transactional
    public void unlikeBoard(User user, Integer boardId) {
        if (!likeRepository.existsByUser_UserIdAndBoard_BoardId(user.getUserId(), boardId)) {
            throw new CustomException(ErrorCode.NOT_LIKED);
        }

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        likeRepository.deleteByUserAndBoard(user, board);
    }

    // DTO 변환 메서드
    private BoardResponseDto convertToResponseDto(Board board, Long userId, boolean likedByUser, boolean scraped) {
        return new BoardResponseDto(
                board.getBoardId(),
                board.getContent(),
                board.getUpdatedAt(),
                board.getPhotoUrls(),
                null, // 댓글 정보는 다른 메서드에서 처리
                likeRepository.countByBoard(board),
                commentRepository.countByBoard_BoardId(board.getBoardId()),
                board.getUser().getNickname(),
                board.getUser().getProfilePictureUrl(),
                board.getUser().getUserId().equals(userId),
                likedByUser,
                scraped
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


}


