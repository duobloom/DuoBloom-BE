package POT.DuoBloom.domain.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionResponseDto {
    private Long emotionId;
    private Integer emoji;
    private LocalDate feedDate;
    private LocalDateTime createdAt;
    private String authorNickname;
    private String authorProfilePictureUrl;
    private boolean mine;
}
