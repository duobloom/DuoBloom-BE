package POT.DuoBloom.board;

import POT.DuoBloom.board.dto.BoardRequestDto;
import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.service.BoardService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/board")
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    // 글 작성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findById(userId);
        Board board = boardService.createBoard(user, boardRequestDTO.getTitle(), boardRequestDTO.getContent());
        BoardResponseDto responseDTO = new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt());
        return ResponseEntity.ok(responseDTO);
    }

    // 전체 글 조회
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        List<BoardResponseDto> responseDTOs = boards.stream()
                .map(board -> new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // 단일 글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findById(userId);

        // 접근 권한 확인
        if (!boardService.canAccessBoard(user)) {
            throw new IllegalStateException("커플인 경우에만 커뮤니티 글을 조회할 수 있습니다.");
        }

        Board board = boardService.getBoardById(boardId)
                .orElseThrow(() -> new IllegalStateException("해당 글이 존재하지 않습니다."));
        BoardResponseDto responseDTO = new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt());
        return ResponseEntity.ok(responseDTO);
    }

    // 글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Integer boardId, @RequestBody BoardRequestDto boardRequestDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findById(userId);
        Board board = boardService.updateBoard(user, boardId, boardRequestDTO.getTitle(), boardRequestDTO.getContent());
        BoardResponseDto responseDTO = new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt());
        return ResponseEntity.ok(responseDTO);
    }

    // 글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User user = userService.findById(userId);
        boardService.deleteBoard(user, boardId);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 추가
    @PostMapping("/{boardId}")
    public ResponseEntity<Void> likeBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findById(userId);
        boardService.likeBoard(user, boardId);
        return ResponseEntity.ok().build();
    }

    // 좋아요 취소
    @PatchMapping("/{boardId}")
    public ResponseEntity<Void> unlikeBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = userService.findById(userId);
        boardService.unlikeBoard(user, boardId);
        return ResponseEntity.ok().build();
    }


}
