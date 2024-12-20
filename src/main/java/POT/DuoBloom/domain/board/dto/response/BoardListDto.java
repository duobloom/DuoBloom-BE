package POT.DuoBloom.domain.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardListDto {
    private Integer boardId;
    private String content;
    private LocalDateTime updatedAt;
    private List<String> photoUrls;
    private int likeCount;
    private int commentCount;
    private String authorNickname;
    private String authorProfilePictureUrl;
    private boolean isMine;
    private boolean likedByUser;
    private boolean scraped;
}
