package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardScrap;
import POT.DuoBloom.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardScrapRepository extends JpaRepository<BoardScrap, Long> {
    List<BoardScrap> findByUser(User user);
    Optional<BoardScrap> findByUserAndBoard(User user, Board board);
}
