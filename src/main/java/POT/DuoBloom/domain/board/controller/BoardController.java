package POT.DuoBloom.domain.board.controller;

import POT.DuoBloom.domain.board.dto.request.BoardRequestDto;
import POT.DuoBloom.domain.board.dto.response.BoardResponseDto;
import POT.DuoBloom.domain.board.service.BoardService;
import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feeds/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    @Operation(summary = "전체 게시글 조회", description = "로그인한 사용자와 그의 커플의 게시글을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        List<BoardResponseDto> boards = boardService.getAllBoards(user);
        return ResponseEntity.ok(boards);
    }

    @Operation(summary = "게시글 작성", description = "게시글을 작성합니다.")
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(@RequestBody BoardRequestDto boardRequestDto,
                                                        HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        BoardResponseDto response = boardService.createBoard(user, boardRequestDto.getContent(), boardRequestDto.getPhotoUrls());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 특정 게시글의 상세 정보를 조회합니다.")
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> getBoardById(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        User user = userService.findById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        BoardResponseDto board = boardService.getBoardDetailsById(boardId, user);
        return ResponseEntity.ok(board);
    }

    @Operation(summary = "게시글 수정", description = "게시글 ID로 특정 게시글을 수정합니다.")
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponseDto> updateBoard(@PathVariable Integer boardId,
                                                        @RequestBody BoardRequestDto boardRequestDto,
                                                        HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        BoardResponseDto updatedBoard = boardService.updateBoard(user, boardId, boardRequestDto);
        return ResponseEntity.ok(updatedBoard);
    }


    @Operation(summary = "게시글 삭제", description = "게시글 ID로 특정 게시글을 삭제합니다.")
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        boardService.deleteBoard(user, boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/like")
    public ResponseEntity<Void> likeBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        User user = userService.findById(userId);
        if (user == null) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 이미 좋아요를 눌렀는지 확인
        if (boardService.isBoardLikedByUser(user, boardId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        boardService.likeBoard(user, boardId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "게시글 좋아요 취소", description = "게시글에 좋아요를 취소합니다.")
    @DeleteMapping("/{boardId}/like")
    public ResponseEntity<Void> unlikeBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);

        // 좋아요를 누르지 않은 게시물인지 확인
        if (!boardService.isBoardLikedByUser(user, boardId)) {
            throw new CustomException(ErrorCode.NOT_LIKED);
        }

        boardService.unlikeBoard(user, boardId);
        return ResponseEntity.noContent().build();
    }


}
