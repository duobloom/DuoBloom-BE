package POT.DuoBloom.emotion.dto;

import POT.DuoBloom.emotion.entity.Emoji;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmotionUpdateDto {
    private Emoji emoji;
    private String content;
}
