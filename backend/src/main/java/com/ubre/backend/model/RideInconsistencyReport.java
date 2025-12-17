package com.ubre.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ride_inconsistency_reports")
public class RideInconsistencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_text", columnDefinition = "TEXT", nullable = false)
    private String reportText;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @ManyToOne
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private RegisteredUser reporter;

    // Constructors
    public RideInconsistencyReport() {
        this.reportedAt = LocalDateTime.now();
    }

    public RideInconsistencyReport(String reportText, Ride ride, RegisteredUser reporter) {
        this.reportText = reportText;
        this.ride = ride;
        this.reporter = reporter;
        this.reportedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportText() {
        return reportText;
    }

    public void setReportText(String reportText) {
        this.reportText = reportText;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public RegisteredUser getReporter() {
        return reporter;
    }

    public void setReporter(RegisteredUser reporter) {
        this.reporter = reporter;
    }
}
