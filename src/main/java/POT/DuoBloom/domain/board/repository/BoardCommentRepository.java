package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.board.entity.BoardComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {
    List<BoardComment> findByBoard_BoardId(Integer boardId);
    int countByBoard_BoardId(Integer boardId);
    int countByBoard(Board board);
}
