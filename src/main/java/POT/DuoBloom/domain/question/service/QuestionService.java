package POT.DuoBloom.domain.question.service;

import POT.DuoBloom.domain.question.dto.response.QuestionWithAnswersDto;
import POT.DuoBloom.domain.question.entity.Answer;
import POT.DuoBloom.domain.question.entity.Question;
import POT.DuoBloom.domain.question.dto.response.AnswerDto;
import POT.DuoBloom.domain.question.repository.AnswerRepository;
import POT.DuoBloom.domain.question.repository.QuestionRepository;
import POT.DuoBloom.domain.user.repository.UserRepository;
import POT.DuoBloom.domain.user.entity.User;
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

    @Transactional(readOnly = true)
    public List<QuestionWithAnswersDto> getQuestionsWithAnswerStatus(LocalDate feedDate, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        User coupleUser = user.getCoupleUser();
        Long coupleUserId = (coupleUser != null) ? coupleUser.getUserId() : null;

        List<Question> questions = questionRepository.findAllByFeedDate(feedDate);
        if (questions.isEmpty()) {
            return List.of();
        }

        return questions.stream().map(question -> {
            List<AnswerDto> answers = Stream.of(userId, coupleUserId)
                    .filter(Objects::nonNull)
                    .flatMap(id -> answerRepository.findByQuestion_QuestionIdAndUser_UserId(question.getQuestionId(), id).stream())
                    .map(answer -> new AnswerDto(
                            answer.getAnswerId(),
                            answer.getQuestion().getQuestionId(),
                            answer.getContent(),
                            answer.getUser().getNickname(),
                            answer.getUser().getProfilePictureUrl(),
                            answer.getUser().getUserId().equals(userId),
                            answer.getCreatedAt(),
                            answer.getUpdatedAt()
                    ))
                    .collect(Collectors.toList());

            boolean myAnswerStatus = answers.stream().anyMatch(a -> a.isMine());
            boolean coupleAnswerStatus = answers.stream().anyMatch(a -> coupleUserId != null && a.isMine() == false);

            return new QuestionWithAnswersDto(
                    question.getQuestionId(),
                    question.getContent(),
                    myAnswerStatus,
                    coupleAnswerStatus,
                    answers
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public void createAnswer(AnswerDto answerDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Question question = questionRepository.findById(answerDto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        Answer answer = new Answer(user, question, answerDto.getContent());
        answerRepository.save(answer);

        user.updateBalance(5);
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
