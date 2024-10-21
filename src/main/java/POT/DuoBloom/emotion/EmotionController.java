package POT.DuoBloom.emotion;

import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.emotion.dto.EmotionUpdateDto;
import POT.DuoBloom.emotion.service.EmotionService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds/emotions")
public class EmotionController {

    private final EmotionService emotionService;
    private final UserService userService;

    // 날짜별 감정 조회
    @GetMapping("/{date}")
    public ResponseEntity<EmotionResponseDto> getEmotionByDate(@PathVariable String date, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User users = userService.findById(userId);
        LocalDate localDate = LocalDate.parse(date);

        return emotionService.findByDateAndUsers(localDate, users)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 감정 생성
    @PostMapping
    public ResponseEntity<Void> createEmotion(@RequestBody EmotionUpdateDto emotionUpdateDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");

        if (userId == null) {
            return ResponseEntity.status(401).build();
        }

        User users = userService.findById(userId);
        LocalDate date = LocalDate.now();

        boolean isCreated = emotionService.createEmotion(date, users, emotionUpdateDto);

        if (!isCreated) {
            return ResponseEntity.status(409).build();  // 이미 감정이 존재
        }

        return ResponseEntity.status(201).build();
    }

    // 감정 수정
    @PatchMapping("/{emotionId}")
    public ResponseEntity<Void> updateEmotion(@PathVariable Long emotionId, @RequestBody EmotionUpdateDto emotionUpdateDto) {
        emotionService.updateEmotion(emotionId, emotionUpdateDto);
        return ResponseEntity.ok().build();
    }
}
