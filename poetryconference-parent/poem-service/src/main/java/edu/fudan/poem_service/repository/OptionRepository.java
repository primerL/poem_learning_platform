package edu.fudan.poem_service.repository;

import edu.fudan.poem_service.entity.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Options, Integer> {
    List<Options> findByQuestionId(int questionId);
}

