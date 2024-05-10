package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.WrongAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WrongAnswerRepository extends JpaRepository<WrongAnswer, Integer> {
}
