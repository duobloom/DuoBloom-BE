package POT.DuoBloom.domain.policy.repository;

import POT.DuoBloom.domain.policy.entity.PolicyScrap;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyScrapRepository extends JpaRepository<PolicyScrap, Long> {
    boolean existsByPolicy_PolicyIdAndUser_UserId(Integer policyId, Long userId);
    //Optional<PolicyScrap> findByPolicy_PolicyIdAndUser_UserId(Integer policyId, Long userId);
    //List<PolicyScrap> findByUser_UserId(Long userId);

    @Query("SELECT ps FROM PolicyScrap ps " +
            "JOIN FETCH ps.policy p " +
            "JOIN FETCH ps.user u " +
            "WHERE p.policyId = :policyId AND u.userId = :userId")
    Optional<PolicyScrap> findByPolicy_PolicyIdAndUser_UserId(
            @Param("policyId") Integer policyId,
            @Param("userId") Long userId
    );

    @Query("SELECT ps FROM PolicyScrap ps " +
            "JOIN FETCH ps.policy p " +
            "JOIN FETCH ps.user u " +
            "WHERE u.userId = :userId")
    List<PolicyScrap> findByUser_UserId(@Param("userId") Long userId);

}
