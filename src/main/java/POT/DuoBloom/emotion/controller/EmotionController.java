package POT.DuoBloom.emotion.controller;

import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.emotion.dto.EmotionUpdateDto;
import POT.DuoBloom.emotion.service.EmotionService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/emotions")
public class EmotionController {

    private final EmotionService emotionService;
    private final UserService userService;

    @Operation(summary = "날짜별 감정 조회", description = "지정한 날짜의 감정을 배열로 조회합니다.")
    @GetMapping("/{date}")
    public ResponseEntity<List<EmotionResponseDto>> getEmotionByDate(@PathVariable String date, HttpSession session) {
        User users = getUserFromSession(session);
        if (users == null) {
            return ResponseEntity.status(401).build();
        }

        LocalDate localDate = LocalDate.parse(date);
        List<EmotionResponseDto> emotions = emotionService.findByFeedDateAndUser(localDate, users);
        return ResponseEntity.ok(emotions);
    }


    @Operation(summary = "감정 생성 또는 수정", description = "지정한 날짜의 감정을 생성하거나 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "감정 생성 또는 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PostMapping
    public ResponseEntity<Void> createOrUpdateEmotion(@RequestBody EmotionUpdateDto emotionUpdateDto, HttpSession session) {
        User user = getUserFromSession(session);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }

        LocalDate feedDate = LocalDate.now();
        emotionService.createOrUpdateEmotion(feedDate, user, emotionUpdateDto.getEmoji());
        return ResponseEntity.status(201).build();
    }


    private User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }

        return userService.findById(userId);
    }
}
