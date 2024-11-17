package POT.DuoBloom.domain.board.controller;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.board.dto.response.BoardListDto;
import POT.DuoBloom.domain.board.service.BoardScrapService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board-scrap")
public class BoardScrapController {

    private final BoardScrapService boardScrapService;
    private final HttpSession httpSession;

    /**
     * 게시글 스크랩
     */
    @PostMapping("/{boardId}")
    public ResponseEntity<String> scrapBoard(@PathVariable Integer boardId) {
        Long userId = getUserIdFromSession();
        boardScrapService.scrapBoard(boardId, userId);
        return ResponseEntity.ok("Board Scrapped");
    }

    /**
     * 게시글 스크랩 취소
     */
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> unscrapBoard(@PathVariable Integer boardId) {
        Long userId = getUserIdFromSession();
        boardScrapService.unscrapBoard(boardId, userId);
        return ResponseEntity.ok("Board Unscrapped");
    }

    /**
     * 스크랩된 게시글 조회
     */
    @GetMapping
    public ResponseEntity<List<BoardListDto>> getScrappedBoards() {
        Long userId = getUserIdFromSession();
        List<BoardListDto> scrappedBoards = boardScrapService.getScrappedBoards(userId);
        return ResponseEntity.ok(scrappedBoards);
    }

    /**
     * 세션에서 사용자 ID 가져오기
     */
    private Long getUserIdFromSession() {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        return userId;
    }
}
