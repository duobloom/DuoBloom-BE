package POT.DuoBloom.board.service;

import POT.DuoBloom.board.dto.BoardListDto;
import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardScrap;
import POT.DuoBloom.board.repository.BoardRepository;
import POT.DuoBloom.board.repository.BoardScrapRepository;
import POT.DuoBloom.board.repository.BoardLikeRepository;
import POT.DuoBloom.board.repository.BoardCommentRepository;
import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardScrapService {

    private final BoardScrapRepository boardScrapRepository;
    private final BoardRepository boardRepository;
    private final BoardLikeRepository likeRepository;
    private final BoardCommentRepository boardCommentRepository;

    @Transactional
    public void scrapBoard(User user, Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        if (boardScrapRepository.findByUserAndBoard(user, board).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_SCRAPPED);
        }
        boardScrapRepository.save(new BoardScrap(user, board));
    }

    @Transactional(readOnly = true)
    public List<BoardListDto> getScrappedBoards(User user) {
        return boardScrapRepository.findByUser(user).stream()
                .map(scrap -> {
                    Board board = scrap.getBoard();

                    // 현재 사용자가 좋아요를 눌렀는지 확인
                    boolean likedByUser = likeRepository.existsByUserAndBoard(user, board);

                    return new BoardListDto(
                            board.getBoardId(),
                            board.getContent(),
                            board.getUpdatedAt(),
                            board.getPhotoUrls(),
                            likeRepository.countByBoard(board),
                            boardCommentRepository.countByBoard_BoardId(board.getBoardId()), // 댓글 수
                            board.getUser().getNickname(),
                            board.getUser().getProfilePictureUrl(),
                            true,
                            likedByUser
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void unsaveBoard(User user, Integer boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomException(ErrorCode.BOARD_NOT_FOUND));
        BoardScrap scrap = boardScrapRepository.findByUserAndBoard(user, board)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
        boardScrapRepository.delete(scrap);
    }
}
