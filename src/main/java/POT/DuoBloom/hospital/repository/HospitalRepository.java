package POT.DuoBloom.hospital.repository;

import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.entity.HospitalType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Integer>, JpaSpecificationExecutor<Hospital> {

}
