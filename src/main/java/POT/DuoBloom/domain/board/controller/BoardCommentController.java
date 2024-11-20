package POT.DuoBloom.domain.board.controller;

import POT.DuoBloom.domain.board.dto.request.BoardCommentRequestDto;
import POT.DuoBloom.domain.board.dto.response.BoardCommentDto;
import POT.DuoBloom.domain.board.entity.BoardComment;
import POT.DuoBloom.domain.board.service.BoardCommentService;
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
    private final BoardCommentService boardCommentService;

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
        BoardComment boardComment = boardCommentService.addComment(user, boardId, requestDto.getContent());

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

    @Operation(summary = "댓글 수정", description = "지정된 댓글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "403", description = "댓글 수정 권한이 없습니다."),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없습니다.")
    })
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<BoardCommentDto> updateComment(@PathVariable Long commentId,
                                                         @RequestBody BoardCommentRequestDto requestDto,
                                                         HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        User user = userService.findById(userId);
        BoardComment updatedComment = boardCommentService.updateComment(commentId, user, requestDto.getContent());

        BoardCommentDto responseDto = new BoardCommentDto(
                updatedComment.getId(),
                user.getNickname(),
                user.getProfilePictureUrl(),
                updatedComment.getContent(),
                updatedComment.getCreatedAt(),
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
        boardCommentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
