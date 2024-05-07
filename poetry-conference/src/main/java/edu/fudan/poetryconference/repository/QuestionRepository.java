package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}
