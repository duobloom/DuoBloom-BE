package POT.DuoBloom.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AnswerDto {
    private Long answerId;
    private Long userId;
    private Long questionId;
    private String content;
    private LocalDate feedDate; // feedDate로 수정
}
