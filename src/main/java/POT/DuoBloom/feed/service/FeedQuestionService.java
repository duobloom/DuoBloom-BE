package POT.DuoBloom.feed.service;

import POT.DuoBloom.feed.dto.AnswerDto;
import POT.DuoBloom.feed.dto.QuestionDto;
import POT.DuoBloom.feed.entity.Answer;
import POT.DuoBloom.feed.entity.Question;
import POT.DuoBloom.feed.mapper.AnswerMapper;
import POT.DuoBloom.feed.mapper.QuestionMapper;
import POT.DuoBloom.feed.repository.AnswerRepository;
import POT.DuoBloom.feed.repository.QuestionRepository;
import POT.DuoBloom.point.pointTransaction.PointTransactionService;
import POT.DuoBloom.point.pointTransaction.TransactionType;
import POT.DuoBloom.user.UserRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedQuestionService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerMapper answerMapper;
    private final QuestionMapper questionMapper;
    private final PointTransactionService pointTransactionService;

    // 특정 날짜의 질문을 조회하여 반환
    public QuestionDto getQuestionByDate(LocalDateTime date) {
        Question question = questionRepository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("No question found for date: " + date));

        // 기본 응답 상태로 미응답 설정
        return questionMapper.toDto(question, "미응답", "미응답");
    }

    // 날짜별 질문 조회와 커플의 응답 상태 및 답변 포함
    public List<QuestionDto> getQuestionsWithAnswerStatus(LocalDate date, Long userId) {
        LocalDateTime dateTime = date.atStartOfDay();
        List<Question> questions = questionRepository.findAllByDate(dateTime);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("해당 날짜에 질문이 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        User coupleUser = user.getCoupleUser();
        Long coupleUserId = (coupleUser != null) ? coupleUser.getUserId() : null;

        return questions.stream()
                .map(question -> {
                    List<AnswerDto> answerDtos = answerRepository.findByQuestion_QuestionId(question.getQuestionId())
                            .stream()
                            .map(answerMapper::toDto)
                            .collect(Collectors.toList());

                    String myAnswerStatus = answerRepository.findByQuestion_QuestionIdAndUser_UserId(question.getQuestionId(), userId).isPresent() ? "응답 완료" : "미응답";
                    String coupleAnswerStatus = (coupleUserId != null && answerRepository.findByQuestion_QuestionIdAndUser_UserId(question.getQuestionId(), coupleUserId).isPresent()) ? "응답 완료" : "미응답";

                    return new QuestionDto(question.getQuestionId(), question.getDate(), question.getContent(), myAnswerStatus, coupleAnswerStatus, answerDtos);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void createAnswer(AnswerDto answerDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 질문을 찾을 수 없습니다."));

        Optional<Answer> existingAnswer = answerRepository.findByQuestion_QuestionIdAndUser_UserId(question.getQuestionId(), userId);
        if (existingAnswer.isPresent()) {
            throw new IllegalStateException("이미 해당 질문에 답변하셨습니다.");
        }

        // Answer 엔티티 생성 및 저장
        Answer answer = answerMapper.toEntity(answerDto, user, question);
        answerRepository.save(answer);

        // 포인트 증가 및 트랜잭션 기록 (ANSWER 타입으로 설정)
        pointTransactionService.increasePoints(user, 5, TransactionType.ANSWER);

        log.info("User {} has answered the question {} and earned 5 points", userId, question.getQuestionId());
    }

    @Transactional
    public void updateAnswer(Long answerId, AnswerDto answerDto, Long userId) {
        Answer answer = answerRepository.findByAnswerIdAndUser_UserId(answerId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found or not authorized"));

        answer.updateContent(answerDto.getContent());
        answerRepository.save(answer);
    }

    @Transactional
    public void deleteAnswer(Long answerId, Long userId) {
        Answer answer = answerRepository.findByAnswerIdAndUser_UserId(answerId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Answer not found or not authorized"));

        answerRepository.delete(answer);
    }
}
