package POT.DuoBloom.domain.board.dto.response;

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
    private String profilePictureUrl;
    private String content;
    private LocalDateTime createdAt;
    private boolean isMine;
}
