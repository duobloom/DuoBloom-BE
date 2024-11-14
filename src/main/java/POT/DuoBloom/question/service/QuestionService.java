package POT.DuoBloom.question.service;

import POT.DuoBloom.question.dto.AnswerDto;
import POT.DuoBloom.question.dto.QuestionDto;
import POT.DuoBloom.question.entity.Answer;
import POT.DuoBloom.question.entity.Question;
import POT.DuoBloom.question.repository.AnswerRepository;
import POT.DuoBloom.question.repository.QuestionRepository;
import POT.DuoBloom.user.repository.UserRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

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
                    List<AnswerDto> answerDtos = Stream.of(userId, coupleUserId)
                            .filter(Objects::nonNull)
                            .flatMap(id -> answerRepository.findByQuestion_QuestionIdAndUser_UserId(question.getQuestionId(), id).stream())
                            .map(answer -> new AnswerDto(
                                    answer.getAnswerId(),
                                    answer.getQuestion().getQuestionId(),
                                    answer.getUser().getUserId(), // userId 설정
                                    answer.getContent(),
                                    answer.getUser().getNickname(),
                                    answer.getUser().getProfilePictureUrl(),
                                    answer.getUser().getUserId().equals(userId), // isMine 설정
                                    answer.getCreatedAt(),
                                    answer.getUpdatedAt()
                            ))
                            .collect(Collectors.toList());

                    boolean myAnswerStatus = answerDtos.stream().anyMatch(a -> a.isMine());
                    boolean coupleAnswerStatus = answerDtos.stream().anyMatch(a -> a.getUserId().equals(coupleUserId));

                    return new QuestionDto(
                            question.getQuestionId(),
                            question.getContent(),
                            myAnswerStatus,
                            coupleAnswerStatus,
                            answerDtos
                    );
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
