package edu.fudan.poem_service.repository;

import edu.fudan.poem_service.entity.Answers;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnswersRepository extends JpaRepository<Answers, Integer> {
    @Query(value = "SELECT * FROM Answers a WHERE a.id = ( " +
            "SELECT subA.id FROM Answers subA " +
            "WHERE subA.user_id = :userId " +
            "ORDER BY subA.review_times ASC " +
            "LIMIT 1) ",
            nativeQuery = true)
    Answers findLeastReviewedQuestionForUser(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("UPDATE Answers a SET a.reviewTimes = a.reviewTimes + 1, a.answerDate = CURRENT_TIMESTAMP WHERE a.id = :id")
    int updateReviewTimesAndAnswerDateById(@Param("id") Long id);

}
