package POT.DuoBloom.region.repository;

import POT.DuoBloom.region.entity.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Long> {
    Detail findByDetailCode(Long detailCode);
}