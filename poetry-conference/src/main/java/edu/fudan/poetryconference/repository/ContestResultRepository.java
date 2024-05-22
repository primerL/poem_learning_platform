package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.dto.UserWinRateDTO;
import edu.fudan.poetryconference.model.ContestResult;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ContestResultRepository extends JpaRepository<ContestResult, Integer> {
    @Query("SELECT cr FROM ContestResult cr WHERE cr.user1Id = :user1Id")
    List<ContestResult> findByUser1Id(Long user1Id);

    @Query("SELECT cr FROM ContestResult cr WHERE (cr.user1Id = :userId ) AND cr.contestDate >= :startDate")
    List<ContestResult> findRecentContestsByUserId(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}
