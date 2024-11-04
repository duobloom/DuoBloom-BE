package POT.DuoBloom.emotion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionResponseDto {
    private Long emotionId;
    private Integer emoji;
    private LocalDate feedDate;
}
