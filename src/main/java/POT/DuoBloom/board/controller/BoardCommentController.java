package POT.DuoBloom.board.controller;

import POT.DuoBloom.board.dto.BoardCommentDto;
import POT.DuoBloom.board.entity.BoardComment;
import POT.DuoBloom.board.service.BoardService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
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

    // 댓글 추가
    @PostMapping("/{boardId}/comment")
    public ResponseEntity<BoardCommentDto> addComment(@PathVariable Integer boardId,
                                                      @RequestBody String content,
                                                      HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = userService.findById(userId);
        BoardComment boardComment = boardService.addComment(user, boardId, content);
        BoardCommentDto responseDto = new BoardCommentDto(boardComment.getId(), user.getNickname(), content, boardComment.getCreatedAt());
        return ResponseEntity.ok(responseDto);
    }

    // 댓글 삭제
    @DeleteMapping("/{boardId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boardService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }


}
