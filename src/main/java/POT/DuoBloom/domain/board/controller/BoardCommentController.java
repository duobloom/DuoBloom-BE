package POT.DuoBloom.domain.board.controller;

import POT.DuoBloom.domain.board.dto.request.BoardCommentRequestDto;
import POT.DuoBloom.domain.board.dto.response.BoardCommentDto;
import POT.DuoBloom.domain.board.entity.BoardComment;
import POT.DuoBloom.domain.board.service.BoardService;
import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.service.UserService;
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
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<BoardCommentDto> addComment(@PathVariable Integer boardId,
                                                      @RequestBody BoardCommentRequestDto requestDto,
                                                      HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        User user = userService.findById(userId);
        BoardComment boardComment = boardService.addComment(user, boardId, requestDto.getContent());

        BoardCommentDto responseDto = new BoardCommentDto(
                boardComment.getId(),
                user.getNickname(),
                user.getProfilePictureUrl(),
                boardComment.getContent(),
                boardComment.getCreatedAt(),
                true
        );
        return ResponseEntity.ok(responseDto);
    }


    @Operation(summary = "댓글 삭제", description = "지정된 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        boardService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
