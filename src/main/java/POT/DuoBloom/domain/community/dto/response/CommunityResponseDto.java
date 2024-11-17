package POT.DuoBloom.domain.community.dto.response;

import POT.DuoBloom.domain.community.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponseDto {
    private Long communityId;
    private String content;
    private Type type;
    private String nickname;
    private String profilePictureUrl;
    private boolean scraped;

}
