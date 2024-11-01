package POT.DuoBloom.question.repository;

import POT.DuoBloom.question.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 답변 ID와 사용자 ID로 답변 조회
    Optional<Answer> findByAnswerIdAndUser_UserId(Long answerId, Long userId);

    // 특정 질문 ID와 사용자 ID로 답변 조회
    Optional<Answer> findByQuestion_QuestionIdAndUser_UserId(Long questionId, Long userId);

    // 특정 질문 ID로 모든 답변 조회 (질문에 달린 모든 답변 목록)
    List<Answer> findByQuestion_QuestionId(Long questionId);
}
