package POT.DuoBloom.domain.question.repository;

import POT.DuoBloom.domain.question.entity.Question;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    //List<Question> findAllByFeedDate(LocalDate feedDate);
    @Query("SELECT q FROM Question q " +
            "LEFT JOIN FETCH q.answers a " +
            "WHERE q.feedDate = :feedDate")
    List<Question> findAllByFeedDate(@Param("feedDate") LocalDate feedDate);

}
