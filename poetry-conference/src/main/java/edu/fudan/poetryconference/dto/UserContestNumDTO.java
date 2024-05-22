package edu.fudan.poetryconference.dto;

public class UserContestNumDTO {
    private Long userId;

    private Integer contestNum;

    public UserContestNumDTO(Long userId, Integer contestNum, Integer weekday) {
        this.userId = userId;
        this.contestNum = contestNum;
    }

    public UserContestNumDTO(Long userId) {
        this.userId = userId;
        this.contestNum = 0;
    }

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
     * 获取 contestNum 的值
     *
     * @return contestNum
     */
    public Integer getContestNum() {
        return contestNum;
    }

    /**
     * 设置  contestNum.
     *
     * @param contestNum contestNum
     */
    public void setContestNum(Integer contestNum) {
        this.contestNum = contestNum;
    }
}
