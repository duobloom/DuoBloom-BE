package POT.DuoBloom.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDto {
    private Long questionId;
    private LocalDate feedDate; // feedDate로 수정
    private String content;
    private String myAnswerStatus;
    private String coupleAnswerStatus;
    private List<AnswerDto> answers;
}
