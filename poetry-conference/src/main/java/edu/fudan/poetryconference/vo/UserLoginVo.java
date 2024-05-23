package edu.fudan.poetryconference.vo;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class UserLoginVo {
    private Long userId;

    private String username;

    private int model;
}
