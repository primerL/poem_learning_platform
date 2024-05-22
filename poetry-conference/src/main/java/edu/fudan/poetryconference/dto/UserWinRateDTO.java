package edu.fudan.poetryconference.dto;

public class UserWinRateDTO {
    private Long userId;

    private Double winRate;

    private String yearMonth;

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
     * 获取 winRate 的值
     *
     * @return winRate
     */
    public Double getWinRate() {
        return winRate;
    }

    /**
     * 设置  winRate.
     *
     * @param winRate winRate
     */
    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }

    /**
     * 获取 yearMonth 的值
     *
     * @return yearMonth
     */
    public String getYearMonth() {
        return yearMonth;
    }

    /**
     * 设置  yearMonth.
     *
     * @param yearMonth yearMonth
     */
    public void setYearMonth(String yearMonth) {
        this.yearMonth = yearMonth;
    }
}
