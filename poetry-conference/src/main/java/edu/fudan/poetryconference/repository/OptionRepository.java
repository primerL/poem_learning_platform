package edu.fudan.poetryconference.repository;

import edu.fudan.poetryconference.model.Options;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Options, Integer> {
    List<Options> findByQuestionId(int questionId);
}
