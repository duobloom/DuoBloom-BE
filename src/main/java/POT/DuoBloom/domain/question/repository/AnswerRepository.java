package POT.DuoBloom.domain.question.repository;

import POT.DuoBloom.domain.question.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {


    // 특정 질문 ID와 사용자 ID로 답변 조회
    List<Answer> findByQuestion_QuestionIdAndUser_UserId(Long questionId, Long userId);



}
