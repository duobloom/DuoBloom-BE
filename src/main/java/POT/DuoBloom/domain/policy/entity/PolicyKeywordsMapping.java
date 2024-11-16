package POT.DuoBloom.domain.policy.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "policy_keywords_mapping")
public class PolicyKeywordsMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "policy_id", nullable = true)
    private Policy policy;

    @ManyToOne
    @JoinColumn(name = "keyword_id", nullable = false)
    private PolicyKeywords keyword;
}
