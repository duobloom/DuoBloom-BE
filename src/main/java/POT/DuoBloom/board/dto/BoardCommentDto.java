package POT.DuoBloom.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardCommentDto {
    private Long id;
    private String nickname;
    private String content;
    private LocalDateTime createdAt;
}
