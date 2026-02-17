package com.example.ubre.ui.dtos;

public class ReportsRequestDto {
    private String dateFrom;
    private String dateTo;
    private String scope;
    private String userEmail;

    public ReportsRequestDto() {}

    public ReportsRequestDto(String dateFrom, String dateTo, String scope, String userEmail) {
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.scope = scope;
        this.userEmail = userEmail;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
