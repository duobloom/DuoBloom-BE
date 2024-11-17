package POT.DuoBloom.domain.hospital.repository;

import POT.DuoBloom.domain.hospital.entity.HospitalScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalScrapRepository extends JpaRepository<HospitalScrap, Long> {

    boolean existsByHospital_HospitalIdAndUser_UserId(Integer hospitalId, Long userId);
    List<HospitalScrap> findByUser_UserId(Long userId);
    Optional<HospitalScrap> findByHospital_HospitalIdAndUser_UserId(Integer hospitalId, Long userId);
}


