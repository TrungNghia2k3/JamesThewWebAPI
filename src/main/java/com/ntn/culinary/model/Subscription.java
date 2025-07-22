package com.ntn.culinary.model;

import java.util.Date;

public class Subscription {
    private int id;
    private int userId;
    private String plan;
    private Date startDate;
    private Date endDate;
    private String paymentStatus;

    public Subscription() {
    }

    public Subscription(int id, int userId, String plan, Date startDate, Date endDate, String paymentStatus) {
        this.id = id;
        this.userId = userId;
        this.plan = plan;
        this.startDate = startDate;
        this.endDate = endDate;
        this.paymentStatus = paymentStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
