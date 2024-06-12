package edu.fudan.user_service.vo;

import lombok.Data;

@Data
public class UserLoginVo {
    private Long userId;

    private String username;

    private int model;
}