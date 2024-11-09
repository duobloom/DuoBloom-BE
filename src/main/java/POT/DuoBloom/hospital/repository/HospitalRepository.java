package POT.DuoBloom.hospital.repository;

import POT.DuoBloom.hospital.entity.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

    List<Hospital> findByDetail(Long detail);
    List<Hospital> findByMiddle(Long middle);
    List<Hospital> findByRegion(Long region);

    List<Hospital> findByRegionAndKeywordMappings_Keyword_Keyword(Long region, POT.DuoBloom.hospital.entity.Keyword keyword);
    List<Hospital> findByKeywordMappings_Keyword_Keyword(POT.DuoBloom.hospital.entity.Keyword keyword);
}
