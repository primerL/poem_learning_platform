package edu.fudan.poetryconference.websocket;

import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Questions, Integer> {
    // 这里可以定义自定义的查询方法
}
