package POT.DuoBloom.domain.policy.repository;

import POT.DuoBloom.domain.policy.entity.PolicyScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyScrapRepository extends JpaRepository<PolicyScrap, Long> {
    boolean existsByPolicy_PolicyIdAndUser_UserId(Integer policyId, Long userId);
    Optional<PolicyScrap> findByPolicy_PolicyIdAndUser_UserId(Integer policyId, Long userId);
    List<PolicyScrap> findByUser_UserId(Long userId);
}
