package POT.DuoBloom.feed.controller;

import POT.DuoBloom.feed.dto.FeedResponseDto;
import POT.DuoBloom.feed.service.FeedService;
import POT.DuoBloom.user.UserRepository;
import POT.DuoBloom.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final UserRepository userRepository;

    @GetMapping("/{date}")
    public ResponseEntity<FeedResponseDto> getDailyFeed(
            @PathVariable LocalDate date,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 세션에서 user를 가져오거나 없으면 userId로 DB에서 조회 후 세션에 저장
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            session.setAttribute("user", user);
        }

        FeedResponseDto feedResponse = feedService.getDailyFeed(date, user.getUserId());

        return ResponseEntity.ok(feedResponse);
    }
}
