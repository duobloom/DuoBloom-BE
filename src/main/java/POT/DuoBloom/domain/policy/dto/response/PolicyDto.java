package POT.DuoBloom.domain.policy.dto.response;

import POT.DuoBloom.domain.policy.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
    private Map<String, Object> target;
    private Map<String, Object> benefit;
    private String linkUrl;
    private String imageUrl;
    private List<KeywordsMappingDto> keywordMappings;
    private boolean scraped;

}
