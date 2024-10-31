package POT.DuoBloom.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class QuestionWithAnswersDto {
    private Long questionId;
    private String content;
    private String myAnswerStatus;
    private String coupleAnswerStatus;
    private List<AnswerDto> answers;
}
