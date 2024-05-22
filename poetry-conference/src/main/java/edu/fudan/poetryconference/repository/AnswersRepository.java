package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.Answers;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AnswersRepository extends JpaRepository<Answers, Integer> {
    @Query(value = "SELECT * FROM Answers a WHERE a.user_id = :userId AND " +
            "a.review_times = (SELECT MIN(subA.review_times) FROM Answers subA WHERE subA.question_id = a.question_id AND subA.user_id = :userId) AND " +
            "a.answer_date = (SELECT MAX(subA.answer_date) FROM Answers subA WHERE subA.question_id = a.question_id AND subA.user_id = :userId AND subA.review_times = a.review_times) " +
            "LIMIT 1", nativeQuery = true)
    Answers findLeastReviewedQuestionForUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Answers a SET a.reviewTimes = a.reviewTimes + 1, a.answerDate = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateReviewTimesAndAnswerDateById(@Param("id") Long id);

}
