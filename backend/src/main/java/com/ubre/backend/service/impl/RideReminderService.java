package com.ubre.backend.service.impl;

import com.ubre.backend.dto.RideDto;
import com.ubre.backend.enums.NotificationType;
import com.ubre.backend.websocket.RideReminderNotification;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;

@Service
public class RideReminderService {

    @Autowired
    @Qualifier("rideTaskScheduler")
    private TaskScheduler scheduler;
    @Autowired
    private WebSocketNotificationService webSocketNotificationService;

    public void start(long userId, LocalDateTime startTime) {
        scheduleNext(userId, startTime);
    }

    private void scheduleNext(long userId, LocalDateTime startTime) {
        long minutesLeft = Duration.between(LocalDateTime.now(), startTime).toMinutes();

        if (minutesLeft <= 0) return; // STOP

        // convert start time in string format HH:mm
        String startTimeStr = startTime.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));

        webSocketNotificationService.sendRideReminder(userId, new RideReminderNotification(
                NotificationType.RIDE_REMINDER.name(), startTimeStr));
        System.out.println("Sent ride reminder to user " + userId + " for ride at " + startTimeStr + ". Minutes left: " + minutesLeft);

        // odredi sledeći interval
        long nextDelayMinutes = minutesLeft > 15 ? 15 : 1;

        scheduler.schedule(
                () -> scheduleNext(userId, startTime),
                Instant.now().plus(Duration.ofMinutes(nextDelayMinutes))
        );
    }

    // one time trigger that is sent via websocket, to user, ride dto
    public void triggerScheduledRide(Long userId, RideDto rideDto, LocalDateTime scheduledTime) {
        // send notification via websocket, at a scheduled time
        scheduler.schedule(
                () -> webSocketNotificationService.sendRideTrigger(userId, rideDto),
                Instant.from(scheduledTime.atZone(ZoneId.systemDefault()))
        );
    }
}
