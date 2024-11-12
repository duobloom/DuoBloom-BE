package POT.DuoBloom.board.repository;

import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    List<Board> findByUserAndFeedDate(User user, LocalDate feedDate);
}
