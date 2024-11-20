package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    @Query("SELECT bc FROM BoardComment bc JOIN FETCH bc.board b JOIN FETCH b.user WHERE b.boardId = :boardId")
    List<BoardComment> findByBoard_BoardId(@Param("boardId") Integer boardId);

    int countByBoard_BoardId(Integer boardId);
    int countByBoard(Board board);
}
