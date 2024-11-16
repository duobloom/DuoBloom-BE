package POT.DuoBloom.domain.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private String content;
    private boolean myAnswerStatus;
    private boolean coupleAnswerStatus;
    private List<AnswerDto> answers;
}
