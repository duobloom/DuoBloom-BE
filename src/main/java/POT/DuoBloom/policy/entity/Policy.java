package POT.DuoBloom.policy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer policyId;

    @Column(name = "policy_name", nullable = false)
    private String policyName;

    private Long region;
    private Long middle;
    private Long detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Sex sex;

    @Column(nullable = true)
    private LocalDate startDate;

    @Column(nullable = true)
    private LocalDate endDate;

    @Column(nullable = true)
    private String target;

    private String linkUrl;



}
