package POT.DuoBloom.policy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapResponseDto {
    private Integer policyId;
    private String policyName;
    private String linkUrl;
}
