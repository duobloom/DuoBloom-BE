package POT.DuoBloom.board.service;

import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.BoardRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

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

    // 전체 글 조회
    @Transactional
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    // 단일 글 조회
    @Transactional
    public Optional<Board> getBoardById(Integer boardId) {
        return boardRepository.findById(boardId);
    }

    // 글 수정
    @Transactional
    public Board updateBoard(User user, Integer boardId, String title, String content) {
        if (!canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티 글을 수정할 수 있습니다.");
        }

        // 기존 게시물을 찾아 업데이트
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        // 작성자 확인
        if (!board.getUser().equals(user)) {
            throw new IllegalStateException("해당 글의 작성자만 수정할 수 있습니다.");
        }

        // 게시물 내용 업데이트
        board = new Board(user, title, content, LocalDateTime.now());
        return boardRepository.save(board);
    }

    // 글 삭제
    @Transactional
    public void deleteBoard(User user, Integer boardId) {
        if (!canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티 글을 삭제할 수 있습니다.");
        }

        // 삭제할 게시물 찾기
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("해당 글이 존재하지 않습니다."));

        // 작성자 확인
        if (!board.getUser().equals(user)) {
            throw new IllegalStateException("해당 글의 작성자만 삭제할 수 있습니다.");
        }

        boardRepository.delete(board);
    }
}
