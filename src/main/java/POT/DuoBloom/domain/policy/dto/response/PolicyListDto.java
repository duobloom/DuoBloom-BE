package POT.DuoBloom.domain.policy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyListDto {
    private Integer policyId;
    private String policyName;
    private String policyHost;
    private String region;
    private String middle;
    private String detail;
    private LocalDate startDate;
    private LocalDate endDate;
    private String imageUrl;
    private List<KeywordsMappingDto> keywordMappings;
    private boolean scraped;
}
