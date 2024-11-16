package POT.DuoBloom.domain.region.repository;

import POT.DuoBloom.domain.region.entity.Middle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MiddleRepository extends JpaRepository<Middle, Long> {
    Middle findByMiddleCode(Long middleCode);

    @Query("SELECT m.name FROM Middle m WHERE m.middleCode = :middleCode")
    Optional<String> findNameByCode(Long middleCode);
}

