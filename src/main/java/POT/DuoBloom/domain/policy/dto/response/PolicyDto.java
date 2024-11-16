package POT.DuoBloom.domain.policy.dto.response;

import POT.DuoBloom.domain.policy.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class PolicyDto {
    private Integer policyId;
    private String policyName;
    private String policyHost;
    private String region;
    private String middle;
    private String detail;
    private Sex sex;
    private LocalDate startDate;
    private LocalDate endDate;
    private String target;
    private String linkUrl;
    private String keyword;
    private List<KeywordsMappingDto> keywordMappings;
}
