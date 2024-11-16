package POT.DuoBloom.domain.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityImageResponseDto {
    private Long imageId;
    private String imageUrl;
}
