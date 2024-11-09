package POT.DuoBloom.community.dto;

import POT.DuoBloom.community.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponseDto {
    private Long communityId;
    private String content;
    private Type type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isOwner;
    private long likeCount;
    private boolean isLikedByUser;
}
