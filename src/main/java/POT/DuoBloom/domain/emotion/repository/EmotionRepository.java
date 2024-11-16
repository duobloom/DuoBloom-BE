package POT.DuoBloom.domain.emotion.repository;

import POT.DuoBloom.domain.emotion.entity.Emotion;
import POT.DuoBloom.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    List<Emotion> findByFeedDateAndUser(LocalDate feedDate, User user);
}
