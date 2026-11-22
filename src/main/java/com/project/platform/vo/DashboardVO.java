package com.project.platform.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class DashboardVO {
    private Integer todayOrderCount; // 今日订单数
    private BigDecimal todayRevenue; // 今日营业额
    private Integer pendingOrderCount; // 待接单数
    private Integer userCount; // 会员总数

    private List<String> trendDates; // 近 7 天日期
    private List<BigDecimal> trendRevenues; // 近 7 天营业额

    private List<Map<String, Object>> hotDishes; // 热销 Top5 (包含 name, sales)
    private List<Map<String, Object>> categoryRatio; // 分类销售占比 (包含 name, value)
    private Integer yesterdayOrderCount;
    private BigDecimal yesterdayRevenue;

    public Integer getYesterdayOrderCount() {
        return yesterdayOrderCount;
    }

    public void setYesterdayOrderCount(Integer yesterdayOrderCount) {
        this.yesterdayOrderCount = yesterdayOrderCount;
    }

    public BigDecimal getYesterdayRevenue() {
        return yesterdayRevenue;
    }

    public void setYesterdayRevenue(BigDecimal yesterdayRevenue) {
        this.yesterdayRevenue = yesterdayRevenue;
    }

    public Integer getTodayOrderCount() {
        return todayOrderCount;
    }

    public void setTodayOrderCount(Integer todayOrderCount) {
        this.todayOrderCount = todayOrderCount;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public Integer getPendingOrderCount() {
        return pendingOrderCount;
    }

    public void setPendingOrderCount(Integer pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public List<String> getTrendDates() {
        return trendDates;
    }

    public void setTrendDates(List<String> trendDates) {
        this.trendDates = trendDates;
    }

    public List<BigDecimal> getTrendRevenues() {
        return trendRevenues;
    }

    public void setTrendRevenues(List<BigDecimal> trendRevenues) {
        this.trendRevenues = trendRevenues;
    }

    public List<Map<String, Object>> getHotDishes() {
        return hotDishes;
    }

    public void setHotDishes(List<Map<String, Object>> hotDishes) {
        this.hotDishes = hotDishes;
    }

    public List<Map<String, Object>> getCategoryRatio() {
        return categoryRatio;
    }

    public void setCategoryRatio(List<Map<String, Object>> categoryRatio) {
        this.categoryRatio = categoryRatio;
    }
}