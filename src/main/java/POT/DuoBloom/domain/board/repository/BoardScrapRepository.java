package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.BoardScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardScrapRepository extends JpaRepository<BoardScrap, Long> {

    boolean existsByBoard_BoardIdAndUser_UserId(Integer boardId, Long userId);

    List<BoardScrap> findByUser_UserId(Long userId);
    Optional<BoardScrap> findByBoard_BoardIdAndUser_UserId(Integer boardId, Long userId);
}
