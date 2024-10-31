package POT.DuoBloom.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {
    private Long answerId;
    private Long userId;
    private LocalDateTime date;
    private String content;
}
