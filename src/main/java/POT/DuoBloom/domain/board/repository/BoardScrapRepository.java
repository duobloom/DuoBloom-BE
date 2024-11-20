package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.BoardScrap;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardScrapRepository extends JpaRepository<BoardScrap, Long> {

    boolean existsByBoard_BoardIdAndUser_UserId(Integer boardId, Long userId);

    //List<BoardScrap> findByUser_UserId(Long userId);
    //Optional<BoardScrap> findByBoard_BoardIdAndUser_UserId(Integer boardId, Long userId);

    @Query("SELECT bs FROM BoardScrap bs " +
            "JOIN FETCH bs.board b " +
            "JOIN FETCH bs.user u " +
            "WHERE u.userId = :userId")
    List<BoardScrap> findByUser_UserId(@Param("userId") Long userId);

    @Query("SELECT bs FROM BoardScrap bs " +
            "JOIN FETCH bs.board b " +
            "JOIN FETCH bs.user u " +
            "WHERE b.boardId = :boardId AND u.userId = :userId")
    Optional<BoardScrap> findByBoard_BoardIdAndUser_UserId(
            @Param("boardId") Integer boardId,
            @Param("userId") Long userId
    );
}
