package POT.DuoBloom.board.controller;

import POT.DuoBloom.board.dto.BoardListDto;
import POT.DuoBloom.board.service.BoardScrapService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/board-scrap")
@RequiredArgsConstructor
public class BoardScrapController {

    private final BoardScrapService boardScrapService;
    private final UserService userService;

    @PostMapping("/{boardId}")
    public void scrapBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        boardScrapService.scrapBoard(user, boardId);
    }

    @GetMapping
    public List<BoardListDto> getScrappedBoards(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        return boardScrapService.getScrappedBoards(user);
    }

    @DeleteMapping("/{boardId}")
    public void unsaveBoard(@PathVariable Integer boardId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        User user = userService.findById(userId);
        boardScrapService.unsaveBoard(user, boardId);
    }
}
