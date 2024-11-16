package POT.DuoBloom.domain.policy.dto.response;

import POT.DuoBloom.domain.policy.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PolicyDto {
    private Integer policyId;
    private String policyName;
    private String policyHost;
    private Long region;
    private Long middle;
    private Long detail;
    private Sex sex;
    private String target;
    private String linkUrl;
    private String keyword;
}
