package POT.DuoBloom.feed.mapper;

import POT.DuoBloom.feed.dto.QuestionDto;
import POT.DuoBloom.feed.entity.Question;

public class QuestionMapper {

    public static QuestionDto toDto(Question question) {
        return new QuestionDto(
                question.getDate(),
                question.getContent()
        );
    }

    public static Question toEntity(QuestionDto questionDto) {
        return new Question(questionDto.getDate(), questionDto.getContent());
    }
}
