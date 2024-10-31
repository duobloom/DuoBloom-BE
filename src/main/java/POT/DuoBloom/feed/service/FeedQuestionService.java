package POT.DuoBloom.feed.service;

import POT.DuoBloom.feed.dto.AnswerDto;
import POT.DuoBloom.feed.dto.QuestionDto;
import POT.DuoBloom.feed.entity.Answer;
import POT.DuoBloom.feed.entity.Question;
import POT.DuoBloom.feed.mapper.AnswerMapper;
import POT.DuoBloom.feed.mapper.QuestionMapper;
import POT.DuoBloom.feed.repository.AnswerRepository;
import POT.DuoBloom.feed.repository.QuestionRepository;
import POT.DuoBloom.user.UserRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FeedQuestionService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    public QuestionDto getQuestionByDate(LocalDateTime date) {
        Question question = questionRepository.findByDate(date)
                .orElseThrow(() -> new IllegalArgumentException("No question found for date: " + date));
        return QuestionMapper.toDto(question);
    }

    @Transactional
    public void createAnswer(AnswerDto answerDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Question question = questionRepository.findByDate(answerDto.getDate())
                .orElseThrow(() -> new IllegalArgumentException("Question not found for date: " + answerDto.getDate()));

        Answer answer = AnswerMapper.toEntity(answerDto, user, question);
        answerRepository.save(answer);

        user.updateBalance(5); // 5포인트 증정
        userRepository.save(user);
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
