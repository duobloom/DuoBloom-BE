package POT.DuoBloom.feed.controller;

import POT.DuoBloom.feed.dto.AnswerDto;
import POT.DuoBloom.feed.dto.QuestionDto;
import POT.DuoBloom.feed.service.FeedQuestionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/feeds")
public class FeedQuestionController {

    private final FeedQuestionService feedQuestionService;

    // 날짜별 공통 질문 조회 - 커플의 응답 상태와 답변 포함
    @GetMapping("/questions/{date}")
    public ResponseEntity<List<QuestionDto>> getQuestionsByDateWithCoupleAnswers(
            @PathVariable LocalDate date, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(feedQuestionService.getQuestionsWithAnswerStatus(date, userId));
    }

    // 공통 질문 답변 작성 - 질문별로 답변
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
    public ResponseEntity<Void> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody AnswerDto answerDto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        feedQuestionService.updateAnswer(answerId, answerDto, userId);
        return ResponseEntity.ok().build();
    }

}
