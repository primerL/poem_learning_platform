package edu.fudan.poetryconference.websocket;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionsRepository extends JpaRepository<Options, Integer> {
    List<Options> findByQuestionId(int questionId);
}
