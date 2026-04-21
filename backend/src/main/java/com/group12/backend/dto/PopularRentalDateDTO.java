package com.group12.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ID20 TODO: 热门租赁日期周榜项。
 */
public class PopularRentalDateDTO {
    private LocalDate date;
    private Integer rank;
    private Integer orderCount;
    private BigDecimal revenue;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(Integer orderCount) {
        this.orderCount = orderCount;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }
}
