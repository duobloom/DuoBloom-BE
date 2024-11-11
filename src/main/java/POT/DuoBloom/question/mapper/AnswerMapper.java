package POT.DuoBloom.question.mapper;

import POT.DuoBloom.question.dto.AnswerDto;
import POT.DuoBloom.question.entity.Answer;
import POT.DuoBloom.question.entity.Question;
import POT.DuoBloom.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {

    public AnswerDto toDto(Answer answer) {
        return new AnswerDto(
                answer.getAnswerId(),
                answer.getQuestion().getQuestionId(),
                answer.getContent(),
                answer.getFeedDate()
        );
    }

    public Answer toEntity(AnswerDto answerDto, User user, Question question) {
        return new Answer(user, question, answerDto.getContent());
    }
}
