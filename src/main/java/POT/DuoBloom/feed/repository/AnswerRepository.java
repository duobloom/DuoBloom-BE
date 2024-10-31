package POT.DuoBloom.feed.repository;

import POT.DuoBloom.feed.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByAnswerIdAndUser_UserId(Long answerId, Long userId);
}
