package POT.DuoBloom.board.controller;

import POT.DuoBloom.board.dto.BoardCommentDto;
import POT.DuoBloom.board.entity.BoardComment;
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

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/boards")
public class BoardCommentController {

    private final BoardService boardService;
    private final UserService userService;

    @Operation(summary = "댓글 추가", description = "지정된 게시물에 댓글을 추가합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 추가 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<BoardCommentDto> addComment(@PathVariable Integer boardId,
                                                      @RequestBody String content,
                                                      HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        BoardComment boardComment = boardService.addComment(user, boardId, content);
        // isMine 항상 true로 설정하여 현재 사용자가 댓글 작성자임을 표시
        BoardCommentDto responseDto = new BoardCommentDto(
                boardComment.getId(),
                user.getNickname(),
                user.getProfilePictureUrl(),
                boardComment.getContent(),
                boardComment.getCreatedAt(),
                true // isMine
        );
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "댓글 삭제", description = "지정된 댓글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
    })
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boardService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
