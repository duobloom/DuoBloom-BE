package POT.DuoBloom.question.mapper;

import POT.DuoBloom.question.dto.AnswerDto;
import POT.DuoBloom.question.dto.QuestionDto;
import POT.DuoBloom.question.entity.Question;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionMapper {

    private final AnswerMapper answerMapper;

    public QuestionMapper(AnswerMapper answerMapper) {
        this.answerMapper = answerMapper;
    }

    public QuestionDto toDto(Question question, String myAnswerStatus, String coupleAnswerStatus) {
        List<AnswerDto> answerDtos = question.getAnswers().stream()
                .map(answerMapper::toDto)
                .collect(Collectors.toList());

        return new QuestionDto(
                question.getQuestionId(),
                question.getFeedDate(), // feedDate로 수정
                question.getContent(),
                myAnswerStatus,
                coupleAnswerStatus,
                answerDtos
        );
    }

    public Question toEntity(QuestionDto questionDto) {
        return new Question(questionDto.getFeedDate(), questionDto.getContent());
    }
}