package edu.fudan.poetryconference.repository;


import edu.fudan.poetryconference.model.ContestResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestResultRepository extends JpaRepository<ContestResult, Integer> {
}