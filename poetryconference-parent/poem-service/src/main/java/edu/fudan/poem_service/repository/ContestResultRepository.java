package edu.fudan.poem_service.repository;

import edu.fudan.poem_service.entity.ContestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ContestResultRepository extends JpaRepository<ContestResult, Integer> {
    @Query("SELECT cr FROM ContestResult cr WHERE cr.user1Id = :user1Id")
    List<ContestResult> findByUser1Id(Long user1Id);

    @Query("SELECT cr FROM ContestResult cr WHERE (cr.user1Id = :userId ) AND cr.contestDate >= :startDate")
    List<ContestResult> findRecentContestsByUserId(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate);
}

