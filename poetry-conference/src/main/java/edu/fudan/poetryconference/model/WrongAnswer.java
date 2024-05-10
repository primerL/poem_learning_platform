package edu.fudan.poetryconference.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "wrong_answers")
public class WrongAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "question_id")
    private Integer questionId;

    @Column(name = "wrong_option")
    private Integer wrongOption;

    @Column(name = "answer_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date answerDate;
}