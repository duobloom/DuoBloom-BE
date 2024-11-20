package POT.DuoBloom.domain.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentResponseDto {
    private Long commentId;
    private String content;
    private String nickname;
    private String profileUrl;
    private boolean isOwner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
