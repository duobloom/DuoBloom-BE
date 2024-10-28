package POT.DuoBloom.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Integer boardId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
}
