package POT.DuoBloom.feed.mapper;

import POT.DuoBloom.feed.dto.AnswerDto;
import POT.DuoBloom.feed.entity.Answer;
import POT.DuoBloom.feed.entity.Question;
import POT.DuoBloom.user.entity.User;

public class AnswerMapper {

    public static AnswerDto toDto(Answer answer) {
        return new AnswerDto(
                answer.getAnswerId(),
                answer.getUser().getUserId(),
                answer.getQuestion().getDate(),
                answer.getContent()
        );
    }

    public static Answer toEntity(AnswerDto answerDto, User user, Question question) {
        return new Answer(user, question, answerDto.getContent());
    }
}
