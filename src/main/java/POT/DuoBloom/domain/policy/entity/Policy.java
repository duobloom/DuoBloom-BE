package POT.DuoBloom.domain.policy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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

    private String target;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "policy_host")
    private String policyHost;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<PolicyKeywordsMapping> policyMappings;
}

