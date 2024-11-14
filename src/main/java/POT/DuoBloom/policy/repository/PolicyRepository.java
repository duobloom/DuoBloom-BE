package POT.DuoBloom.policy.repository;

import POT.DuoBloom.policy.entity.Keyword;
import POT.DuoBloom.policy.entity.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Integer> {

    @Query("SELECT p FROM Policy p " +
            "JOIN p.policyMappings pm " +
            "JOIN pm.keyword k " +
            "WHERE (:region IS NULL OR p.region = :region) " +
            "AND (:middle IS NULL OR p.middle = :middle) " +
            "AND (:detail IS NULL OR p.detail = :detail) " +
            "AND (:keyword IS NULL OR k.keyword = :keyword)")
    List<Policy> findByRegionAndKeyword(
            @Param("region") Long region,
            @Param("middle") Long middle,
            @Param("detail") Long detail,
            @Param("keyword") Keyword keyword
    );

}

