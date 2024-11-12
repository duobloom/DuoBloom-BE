package POT.DuoBloom.question.service;

import POT.DuoBloom.question.dto.AnswerDto;
import POT.DuoBloom.question.dto.QuestionDto;
import POT.DuoBloom.question.entity.Answer;
import POT.DuoBloom.question.entity.Question;
import POT.DuoBloom.question.mapper.AnswerMapper;
import POT.DuoBloom.question.repository.AnswerRepository;
import POT.DuoBloom.question.repository.QuestionRepository;
import POT.DuoBloom.user.repository.UserRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerMapper answerMapper;

    // 날짜별 질문 조회와 커플의 응답 상태 및 답변 포함
    @Cacheable(value = "questionsWithAnswers", key = "#feedDate.toString() + #userId")
    public List<QuestionDto> getQuestionsWithAnswerStatus(LocalDate feedDate, Long userId) {
        List<Question> questions = questionRepository.findAllByFeedDate(feedDate);
        if (questions.isEmpty()) {
            return List.of();
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        User coupleUser = user.getCoupleUser();
        Long coupleUserId = coupleUser != null ? coupleUser.getUserId() : null;

        return questions.stream()
                .map(question -> {
                    List<Answer> answers = answerRepository.findByQuestion_QuestionId(question.getQuestionId());
                    List<AnswerDto> answerDtos = answers.stream()
                            .map(answer -> answerMapper.toDto(answer, userId))  // 수정된 매퍼 사용
                            .collect(Collectors.toList());

                    String myAnswerStatus = answers.stream().anyMatch(a -> a.getUser().getUserId().equals(userId)) ? "응답 완료" : "미응답";
                    String coupleAnswerStatus = answers.stream().anyMatch(a -> a.getUser().getUserId().equals(coupleUserId)) ? "응답 완료" : "미응답";

                    return new QuestionDto(question.getQuestionId(), question.getFeedDate(), question.getContent(), myAnswerStatus, coupleAnswerStatus, answerDtos);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createAnswer(AnswerDto answerDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Answer answer = new Answer(user, question, answerDto.getContent());
        answerRepository.save(answer);

        increasePoints(user, 5);
    }

    @Async
    public void increasePoints(User user, int points) {
        // 포인트 증가 로직 및 로깅
        log.info("User {} has earned {} points", user.getUserId(), points);
    }

    @Transactional
    public void updateAnswer(Long answerId, AnswerDto answerDto, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found or not authorized"));

        if (!answer.getUser().getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to update this answer");
        }

        answer.updateContent(answerDto.getContent());
        answerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new RuntimeException("Answer not found or not authorized"));

        if (!answer.getUser().getUserId().equals(userId)) {
            throw new SecurityException("User not authorized to delete this answer");
        }

        answerRepository.delete(answer);
    }
}
