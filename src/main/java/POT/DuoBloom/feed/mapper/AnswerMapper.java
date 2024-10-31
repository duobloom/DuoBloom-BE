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
                answer.getUser().getUserId(), // userId로 수정
                answer.getQuestion().getQuestionId(), // questionId 추가
                answer.getContent(),
                answer.getCreatedAt() // 작성 시간
        );
    }

    public Answer toEntity(AnswerDto answerDto, User user, Question question) {
        return new Answer(user, question, answerDto.getContent());
    }
}
