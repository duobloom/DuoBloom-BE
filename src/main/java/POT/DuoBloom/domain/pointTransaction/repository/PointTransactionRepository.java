package POT.DuoBloom.domain.pointTransaction.repository;

import POT.DuoBloom.domain.pointTransaction.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByUser_UserId(Long userId);
}
