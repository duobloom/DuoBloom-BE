package POT.DuoBloom.emotion;

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
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/emotions")
public class EmotionController {

    private final EmotionService emotionService;
    private final UserService userService;

    @Operation(summary = "날짜별 감정 조회", description = "지정한 날짜의 감정을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "감정 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @GetMapping("/{date}")
    public ResponseEntity<?> getEmotionByDate(@PathVariable String date, HttpSession session) {
        User users = getUserFromSession(session);
        if (users == null) {
            return ResponseEntity.status(401).build();
        }

        LocalDate localDate = LocalDate.parse(date);
        return ResponseEntity.ok(emotionService.findByFeedDateAndUsers(localDate, users));
    }

    @Operation(summary = "감정 생성", description = "오늘 날짜의 감정을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "감정 생성 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "409", description = "이미 해당 날짜에 감정이 존재합니다.")
    })
    @PostMapping
    public ResponseEntity<Void> createEmotion(@RequestBody EmotionUpdateDto emotionUpdateDto, HttpSession session) {
        User users = getUserFromSession(session);
        if (users == null) {
            return ResponseEntity.status(401).build();
        }

        LocalDate date = LocalDate.now();
        boolean isCreated = emotionService.createEmotion(date, users, emotionUpdateDto);

        if (!isCreated) {
            return ResponseEntity.status(409).build();  // 이미 감정이 존재
        }

        return ResponseEntity.status(201).build();
    }

    @Operation(summary = "감정 수정", description = "감정의 내용을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "감정 수정 성공"),
            @ApiResponse(responseCode = "404", description = "지정한 감정을 찾을 수 없습니다.")
    })
    @PatchMapping("/{emotionId}")
    public ResponseEntity<Void> updateEmotion(@PathVariable Long emotionId, @RequestBody EmotionUpdateDto emotionUpdateDto) {
        emotionService.updateEmotion(emotionId, emotionUpdateDto);
        return ResponseEntity.ok().build();
    }

    private User getUserFromSession(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return null;
        }

        return userService.findById(userId);
    }
}
