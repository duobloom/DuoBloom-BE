package POT.DuoBloom.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Integer boardId;
    private String content;
    private LocalDateTime updatedAt;
    private List<String> photoUrls;
    private List<BoardCommentDto> comments;
    private int likeCount;
    private int commentCount;
    private String authorNickname;
    private String authorProfilePictureUrl;
    private boolean isMine;
}
