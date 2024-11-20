package POT.DuoBloom.domain.board.repository;

import POT.DuoBloom.domain.board.entity.Board;
import POT.DuoBloom.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {
    //List<Board> findByUserAndFeedDate(User user, LocalDate feedDate);

    @Query("SELECT b FROM Board b " +
            "JOIN FETCH b.user " +
            "LEFT JOIN FETCH b.photoUrls " +
            "WHERE b.user = :user AND b.feedDate = :feedDate")
    List<Board> findByUserAndFeedDate(
            @Param("user") User user,
            @Param("feedDate") LocalDate feedDate
    );
}
