package POT.DuoBloom.domain.region.repository;

import POT.DuoBloom.domain.region.entity.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Long> {
    Detail findByDetailCode(Long detailCode);

    @Query("SELECT d.name FROM Detail d WHERE d.detailCode = :detailCode")
    Optional<String> findNameByCode(Long detailCode);
}