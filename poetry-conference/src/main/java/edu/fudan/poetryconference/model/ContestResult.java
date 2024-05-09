package edu.fudan.poetryconference.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "contest_results")
public class ContestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user1_name")
    private String user1Name;

    @Column(name = "user2_name")
    private String user2Name;

    @Column(name = "score_1")
    private int score1;

    @Column(name = "score_2")
    private int score2;

    @Column(name = "winner_name")
    private String winnerName;

    @Column(name = "contest_date")
    private Date contestDate;
}
