package POT.DuoBloom.domain.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityLikeResponseDto {
    private Long userId;
    private String nickname;
}
