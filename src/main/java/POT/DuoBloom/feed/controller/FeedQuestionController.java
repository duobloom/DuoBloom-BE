package POT.DuoBloom.feed.controller;

import POT.DuoBloom.feed.dto.AnswerDto;
import POT.DuoBloom.feed.dto.QuestionDto;
import POT.DuoBloom.feed.service.FeedQuestionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedQuestionController {

    private final FeedQuestionService feedQuestionService;

    // 날짜별 공통 질문 조회
    @GetMapping("/questions/{date}")
    public ResponseEntity<QuestionDto> getQuestionByDate(@PathVariable LocalDate date) {
        // LocalDate를 LocalDateTime으로 변환하여 서비스로 전달
        LocalDateTime dateTime = date.atStartOfDay();
        return ResponseEntity.ok(feedQuestionService.getQuestionByDate(dateTime));
    }

    // 공통 질문 답변 작성
    @PostMapping("/answer")
    public ResponseEntity<Void> postAnswer(@RequestBody AnswerDto answerDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        feedQuestionService.createAnswer(answerDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 공통 질문 답변 수정
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<Void> updateAnswer(@PathVariable Long answerId, @RequestBody AnswerDto answerDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        feedQuestionService.updateAnswer(answerId, answerDto, userId);
        return ResponseEntity.ok().build();
    }

    // 공통 질문 답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        feedQuestionService.deleteAnswer(answerId, userId);
        return ResponseEntity.noContent().build();
    }
}
