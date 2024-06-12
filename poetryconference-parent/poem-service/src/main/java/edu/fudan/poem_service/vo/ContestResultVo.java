package edu.fudan.poem_service.vo;

import java.time.LocalDateTime;

public class ContestResultVo {
    private Long userId;
    private Long score;
    private LocalDateTime time;

    // Getters and Setters
    /**
     * 获取 userId 的值
     *
     * @return userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置  userId.
     *
     * @param userId userId
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取 score 的值
     *
     * @return score
     */
    public Long getScore() {
        return score;
    }

    /**
     * 设置  score.
     *
     * @param score score
     */
    public void setScore(Long score) {
        this.score = score;
    }

    /**
     * 获取 time 的值
     *
     * @return time
     */
    public LocalDateTime getTime() {
        return time;
    }

    /**
     * 设置  time.
     *
     * @param time time
     */
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
