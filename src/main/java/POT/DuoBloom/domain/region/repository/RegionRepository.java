package POT.DuoBloom.domain.region.repository;

import POT.DuoBloom.domain.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Region findByRegionCode(Long regionCode);

    @Query("SELECT r.name FROM Region r WHERE r.regionCode = :regionCode")
    Optional<String> findNameByCode(Long regionCode);
}
