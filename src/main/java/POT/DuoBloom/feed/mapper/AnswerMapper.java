package POT.DuoBloom.feed.mapper;

import POT.DuoBloom.feed.dto.AnswerDto;
import POT.DuoBloom.feed.entity.Answer;
import POT.DuoBloom.feed.entity.Question;
import POT.DuoBloom.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AnswerMapper {

    public AnswerDto toDto(Answer answer) {
        return new AnswerDto(
                answer.getAnswerId(),
                answer.getUser().getUserId(),
                answer.getQuestion().getQuestionId(),
                answer.getContent(),
                answer.getFeedDate() // feedDate로 수정
        );
    }

    public Answer toEntity(AnswerDto answerDto, User user, Question question) {
        return new Answer(user, question, answerDto.getContent());
    }
}
