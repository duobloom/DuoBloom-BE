package POT.DuoBloom.board.repository;

import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardLike;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    boolean existsByUserAndBoard(User user, Board board);
    void deleteByUserAndBoard(User user, Board board);
    int countByBoard(Board board); // 좋아요 수 계산 메서드 추가
}
