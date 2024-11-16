package POT.DuoBloom.board.repository;

import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardScrap;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardScrapRepository extends JpaRepository<BoardScrap, Long> {
    List<BoardScrap> findByUser(User user);
    Optional<BoardScrap> findByUserAndBoard(User user, Board board);
}
