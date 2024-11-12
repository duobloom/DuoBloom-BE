package POT.DuoBloom.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class AnswerDto {
    private Long answerId;
    private Long questionId;
    private String content;
    private String nickname;
    private String profilePictureUrl;
    private boolean isMine;
}
