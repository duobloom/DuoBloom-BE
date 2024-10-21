package POT.DuoBloom.emotion.dto;

import POT.DuoBloom.emotion.Emoji;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionResponseDto {
    private Long emotionId;
    private Emoji emoji;
    private String content;
    private LocalDate date;
}
