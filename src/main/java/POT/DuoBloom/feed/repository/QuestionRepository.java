package POT.DuoBloom.feed.repository;

import POT.DuoBloom.feed.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, LocalDateTime> {
    Optional<Question> findByDate(LocalDateTime date);
}
