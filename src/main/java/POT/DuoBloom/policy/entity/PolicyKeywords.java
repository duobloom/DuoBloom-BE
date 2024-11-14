package POT.DuoBloom.policy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "policy_keywords")
public class PolicyKeywords {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "keyword_id")
    private Long keywordId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private Keyword keyword;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL)
    private List<PolicyKeywordsMapping> policyMappings;
}
