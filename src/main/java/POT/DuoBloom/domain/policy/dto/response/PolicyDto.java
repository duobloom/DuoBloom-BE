package POT.DuoBloom.domain.policy.dto.response;

import POT.DuoBloom.domain.policy.entity.Sex;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;
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

    public static Map<String, Object> convertJsonToMap(String json) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public void setTarget(String targetJson) {
        this.target = convertJsonToMap(targetJson);
    }

    public void setBenefit(String benefitJson) {
        this.benefit = convertJsonToMap(benefitJson);
    }
}
