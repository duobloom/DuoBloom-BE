package POT.DuoBloom.point.pointTransaction.repository;

import POT.DuoBloom.point.pointTransaction.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    List<PointTransaction> findByUser_UserId(Long userId);
}
