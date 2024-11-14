package POT.DuoBloom.policy.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
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
