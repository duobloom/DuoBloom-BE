package POT.DuoBloom.domain.hospital.repository;

import POT.DuoBloom.domain.hospital.entity.Hospital;
import POT.DuoBloom.domain.hospital.entity.HospitalType;
import POT.DuoBloom.domain.hospital.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    List<Hospital> findByHospitalNameContaining(String name);

    @Query("SELECT DISTINCT h FROM Hospital h " +
            "LEFT JOIN FETCH h.keywordMappings km " +
            "LEFT JOIN FETCH km.keyword k " +
            "WHERE (:region IS NULL OR h.region = :region) " +
            "AND (:middle IS NULL OR h.middle = :middle) " +
            "AND (:detail IS NULL OR h.detail = :detail) " +
            "AND (:type IS NULL OR h.type = :type) " +
            "AND (:keyword IS NULL OR k.keyword = :keyword)")
    List<Hospital> findHospitalsByFilters(
            @Param("region") Long region,
            @Param("middle") Long middle,
            @Param("detail") Long detail,
            @Param("keyword") Keyword keyword,
            @Param("type") HospitalType type);


    @Query("SELECT CASE WHEN COUNT(hs) > 0 THEN true ELSE false END " +
            "FROM HospitalScrap hs " +
            "WHERE hs.hospital.hospitalId = :hospitalId AND hs.user.userId = :userId")
    boolean existsByHospitalIdAndUserId(@Param("hospitalId") Integer hospitalId, @Param("userId") Long userId);

}
