package POT.DuoBloom.board.repository;

import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardLike;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByUserAndBoard(User user, Board board);
    void deleteByUserAndBoard(User user, Board board);
}
