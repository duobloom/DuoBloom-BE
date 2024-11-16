package POT.DuoBloom.policy.repository;

import POT.DuoBloom.policy.entity.Policy;
import POT.DuoBloom.policy.entity.PolicyScrap;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyScrapRepository extends JpaRepository<PolicyScrap, Long> {
    List<PolicyScrap> findByUser(User user);
    Optional<PolicyScrap> findByUserAndPolicy(User user, Policy policy);
}
