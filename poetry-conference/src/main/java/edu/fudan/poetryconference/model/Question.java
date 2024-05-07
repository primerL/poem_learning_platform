package edu.fudan.poetryconference.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionId;

    private String content;

    private int answer;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Option> options;
}