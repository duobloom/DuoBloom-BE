package POT.DuoBloom.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private LocalDateTime date;
    private String content;
    private String myAnswerStatus;
    private String coupleAnswerStatus;
    private List<AnswerDto> answers;
}

