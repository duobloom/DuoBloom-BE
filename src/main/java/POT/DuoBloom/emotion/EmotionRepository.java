package POT.DuoBloom.emotion;

import POT.DuoBloom.emotion.entity.Emotion;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface EmotionRepository extends JpaRepository<Emotion, Long> {
    Optional<Emotion> findByFeedDateAndUsers(LocalDate feedDate, User users);
}
