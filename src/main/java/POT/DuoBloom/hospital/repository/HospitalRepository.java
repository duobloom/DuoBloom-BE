package POT.DuoBloom.hospital.repository;

import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.entity.HospitalType;
import POT.DuoBloom.hospital.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    @Query("SELECT h FROM Hospital h " +
            "LEFT JOIN h.keywordMappings km " +
            "WHERE (:region IS NULL OR h.region = :region) " +
            "AND (:middle IS NULL OR h.middle = :middle) " +
            "AND (:detail IS NULL OR h.detail = :detail) " +
            "AND (:type IS NULL OR h.type = :type) " +
            "AND (:keyword IS NULL OR km.keyword.keyword = :keyword)")
    List<Hospital> findHospitalsByFilters(
            @Param("region") Long region,
            @Param("middle") Long middle,
            @Param("detail") Long detail,
            @Param("keyword") Keyword keyword,
            @Param("type") HospitalType type);
}
