package POT.DuoBloom.question.repository;

import POT.DuoBloom.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findAllByFeedDate(LocalDate feedDate);
    Optional<Question> findByFeedDate(LocalDate feedDate);
}
