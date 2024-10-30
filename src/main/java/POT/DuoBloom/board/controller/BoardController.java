package POT.DuoBloom.board.controller;

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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/boards")
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    // 글 작성
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        if (!boardService.canAccessBoard(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Board board = boardService.createBoard(user, boardRequestDTO.getTitle(), boardRequestDTO.getContent());
        BoardResponseDto responseDTO = new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt(), null);
        return ResponseEntity.ok(responseDTO);
    }

    // 전체 글 조회
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        List<BoardResponseDto> responseDTOs = boards.stream()
                .map(board -> new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt(), null))
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    // 단일 글 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        if (!boardService.canAccessBoard(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        BoardResponseDto responseDTO = boardService.getBoardDetailsById(boardId);
        return ResponseEntity.ok(responseDTO);
    }

    // 글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Integer boardId, @RequestBody BoardRequestDto boardRequestDTO, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        if (!boardService.canAccessBoard(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Board board = boardService.updateBoard(user, boardId, boardRequestDTO.getTitle(), boardRequestDTO.getContent());
        BoardResponseDto responseDTO = new BoardResponseDto(board.getBoardId(), board.getTitle(), board.getContent(), board.getUpdatedAt(), null);
        return ResponseEntity.ok(responseDTO);
    }

    // 글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        if (!boardService.canAccessBoard(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        boardService.deleteBoard(user, boardId);
        return ResponseEntity.noContent().build();
    }

    // 좋아요 추가
    @PostMapping("/{boardId}/like")
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
    @PatchMapping("/{boardId}/unlike")
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
