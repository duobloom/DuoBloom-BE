package POT.DuoBloom.domain.emotion.repository;

import POT.DuoBloom.domain.emotion.entity.Emotion;
import POT.DuoBloom.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    //List<Emotion> findByFeedDateAndUser(LocalDate feedDate, User user);

    @Query("SELECT e FROM Emotion e " +
            "JOIN FETCH e.user u " +
            "WHERE e.feedDate = :feedDate AND u = :user")
    List<Emotion> findByFeedDateAndUser(
            @Param("feedDate") LocalDate feedDate,
            @Param("user") User user
    );
}
