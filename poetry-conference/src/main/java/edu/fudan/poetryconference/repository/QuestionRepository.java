package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Questions, Integer> {
}
