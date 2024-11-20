package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardLike;
import POT.DuoBloom.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    int countByBoard(Board board);
    boolean existsByUserAndBoard(User user, Board board);
    void deleteByUserAndBoard(User user, Board board);

    @Query("SELECT COUNT(bl) > 0 FROM BoardLike bl WHERE bl.user.userId = :userId AND bl.board.boardId = :boardId")
    boolean existsByUser_UserIdAndBoard_BoardId(@Param("userId") Long userId, @Param("boardId") Integer boardId);

}
