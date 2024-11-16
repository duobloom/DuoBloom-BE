package POT.DuoBloom.domain.hospital.repository;

import POT.DuoBloom.domain.hospital.entity.Hospital;
import POT.DuoBloom.domain.hospital.entity.HospitalScrap;
import POT.DuoBloom.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HospitalScrapRepository extends JpaRepository<HospitalScrap, Long> {
    List<HospitalScrap> findByUser(User user);
    Optional<HospitalScrap> findByUserAndHospital(User user, Hospital hospital); // 추가
}
