package POT.DuoBloom.domain.question.controller;

import POT.DuoBloom.domain.question.dto.response.AnswerDto;
import POT.DuoBloom.domain.question.dto.response.QuestionWithAnswersDto;
import POT.DuoBloom.domain.question.service.QuestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "날짜별 공통 질문 조회", description = "지정된 날짜의 공통 질문을 커플의 응답 상태와 함께 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공통 질문 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @GetMapping("/questions/{date}")
    public ResponseEntity<List<QuestionWithAnswersDto>> getQuestionsByDateWithCoupleAnswers(
            @PathVariable LocalDate date, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<QuestionWithAnswersDto> questionDtos = questionService.getQuestionsWithAnswerStatus(date, userId);
        return ResponseEntity.ok(questionDtos);
    }

    @Operation(summary = "공통 질문 답변 작성", description = "지정된 질문에 대한 답변을 작성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "답변 작성 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @PostMapping("/answer")
    public ResponseEntity<Void> postAnswer(@RequestBody AnswerDto answerDto, HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        questionService.createAnswer(answerDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "공통 질문 답변 수정", description = "지정된 답변을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "답변 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "해당 답변을 찾을 수 없습니다.")
    })
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<Void> updateAnswer(
            @PathVariable Long answerId,
            @RequestBody AnswerDto answerDto,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        questionService.updateAnswer(answerId, answerDto, userId);
        return ResponseEntity.ok().build();
    }
}
