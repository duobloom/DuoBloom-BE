package POT.DuoBloom.region.repository;

import POT.DuoBloom.region.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
    Region findByRegionCode(Long regionCode);
}
