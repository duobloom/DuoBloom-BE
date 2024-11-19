package POT.DuoBloom.domain.policy.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "policy")
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "policy_id")
    private Integer policyId;

    @Column(name = "policy_name", nullable = false)
    private String policyName;

    private Long region;
    private Long middle;
    private Long detail;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex")
    private Sex sex;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Lob
    @Column(name = "target", columnDefinition = "json")
    private String target;

    @Lob
    @Column(name = "benefit", columnDefinition = "json")
    private String benefit;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "policy_host")
    private String policyHost;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonManagedReference
    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PolicyKeywordsMapping> policyMappings;

    public Map<String, Object> getBenefitAsMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(benefit, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse benefit JSON", e);
        }
    }

    public Map<String, Object> getTargetAsMap() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(target, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse benefit JSON", e);
        }
    }
}

