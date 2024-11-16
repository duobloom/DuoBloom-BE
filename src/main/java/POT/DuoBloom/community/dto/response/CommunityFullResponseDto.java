package POT.DuoBloom.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityFullResponseDto {
    private CommunityResponseDto community;
    private List<CommunityImageResponseDto> images;
    private List<CommunityCommentResponseDto> comments;
    private long likeCount;
    private boolean isLikedByUser;
    private boolean isOwner;
}
