package POT.DuoBloom.domain.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnswerDto {
    private Long answerId;
    private Long questionId;
    private String content;
    private String nickname;
    private String profilePictureUrl;
    private boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
