package POT.DuoBloom.board.controller;

import POT.DuoBloom.board.dto.BoardRequestDto;
import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.service.BoardService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/boards")
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    @Operation(summary = "전체 글 조회", description = "모든 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "글 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getAllBoards() {
        List<BoardResponseDto> responseDTOs = boardService.getAllBoards();
        return ResponseEntity.ok(responseDTOs);
    }

    @Operation(summary = "단일 글 조회", description = "특정 게시글을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "글 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다.")
    })
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

    @Operation(summary = "글 작성", description = "새로운 글을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "글 작성 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다.")
    })
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

        Board board = boardService.createBoard(user, boardRequestDTO.getTitle(), boardRequestDTO.getContent(), boardRequestDTO.getPhotoUrls());
        BoardResponseDto responseDTO = new BoardResponseDto(
                board.getBoardId(), board.getTitle(), board.getContent(),
                board.getUpdatedAt(), board.getPhotoUrls(), null, 0, 0
        );
        return ResponseEntity.ok(responseDTO);
    }



    @Operation(summary = "글 수정", description = "특정 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다.")
    })
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
        BoardResponseDto responseDTO = new BoardResponseDto(
                board.getBoardId(), board.getTitle(), board.getContent(),
                board.getUpdatedAt(), board.getPhotoUrls(), null, 0, 0
        );
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "글 삭제", description = "특정 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "권한이 없습니다.")
    })
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

    @Operation(summary = "게시글 좋아요", description = "특정 게시글에 좋아요를 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
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

    @Operation(summary = "게시글 좋아요 취소", description = "특정 게시글에 대한 좋아요를 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
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
