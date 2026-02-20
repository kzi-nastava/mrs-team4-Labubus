package com.example.ubre.ui.dtos;

public class ReportSummaryDto {
    private int totalRides;
    private double totalDistanceKm;
    private double totalAmountMoney;
    private double averageRidesPerDay;
    private double averageDistancePerDay;
    private double averageMoneyPerDay;

    public ReportSummaryDto() {}

    public int getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(int totalRides) {
        this.totalRides = totalRides;
    }

    public double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    public void setTotalDistanceKm(double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    public double getTotalAmountMoney() {
        return totalAmountMoney;
    }

    public void setTotalAmountMoney(double totalAmountMoney) {
        this.totalAmountMoney = totalAmountMoney;
    }

    public double getAverageRidesPerDay() {
        return averageRidesPerDay;
    }

    public void setAverageRidesPerDay(double averageRidesPerDay) {
        this.averageRidesPerDay = averageRidesPerDay;
    }

    public double getAverageDistancePerDay() {
        return averageDistancePerDay;
    }

    public void setAverageDistancePerDay(double averageDistancePerDay) {
        this.averageDistancePerDay = averageDistancePerDay;
    }

    public double getAverageMoneyPerDay() {
        return averageMoneyPerDay;
    }

    public void setAverageMoneyPerDay(double averageMoneyPerDay) {
        this.averageMoneyPerDay = averageMoneyPerDay;
    }
}
