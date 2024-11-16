package POT.DuoBloom.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityCommentResponseDto {
    private Long commentId;
    private String content;
    private String nickname;
    private String profileUrl;
    private boolean isOwner;
}
