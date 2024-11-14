package POT.DuoBloom.question.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class AnswerDto {
    private Long answerId;
    private Long questionId;
    private Long userId; // 작성자 ID 필드 추가
    private String content;
    private String nickname;
    private String profilePictureUrl;
    private boolean isMine;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
