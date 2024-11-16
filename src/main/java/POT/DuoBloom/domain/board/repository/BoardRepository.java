package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByUserAndFeedDate(User user, LocalDate feedDate);
}
