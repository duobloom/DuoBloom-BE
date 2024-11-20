package POT.DuoBloom.domain.question.repository;

import POT.DuoBloom.domain.question.entity.Answer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 질문 ID와 사용자 ID로 답변 조회
    //List<Answer> findByQuestion_QuestionIdAndUser_UserId(Long questionId, Long userId);

    @Query("SELECT a FROM Answer a " +
            "JOIN FETCH a.question q " +
            "JOIN FETCH a.user u " +
            "WHERE q.questionId = :questionId AND u.userId = :userId")
    List<Answer> findByQuestion_QuestionIdAndUser_UserId(
            @Param("questionId") Long questionId,
            @Param("userId") Long userId
    );



}
