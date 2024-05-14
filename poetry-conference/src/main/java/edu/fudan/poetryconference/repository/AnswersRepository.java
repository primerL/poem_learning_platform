package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.Answers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswersRepository extends JpaRepository<Answers, Integer> {
}
