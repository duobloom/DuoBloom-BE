package POT.DuoBloom.domain.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class QuestionWithAnswersDto {
    private Long questionId;
    private String content;
    private boolean myAnswerStatus;
    private boolean coupleAnswerStatus;
    private List<AnswerDto> answers;
}
