package POT.DuoBloom.region.repository;

import POT.DuoBloom.region.entity.Middle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiddleRepository extends JpaRepository<Middle, Long> {
    Middle findByMiddleCode(Long middleCode);
}

