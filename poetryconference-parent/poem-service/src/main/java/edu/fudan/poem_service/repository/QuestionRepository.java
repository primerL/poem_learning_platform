package edu.fudan.poem_service.repository;

import edu.fudan.poem_service.entity.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Questions, Integer> {
}

