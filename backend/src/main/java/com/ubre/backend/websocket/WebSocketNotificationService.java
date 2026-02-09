package com.ubre.backend.websocket;

import com.ubre.backend.dto.CancellationDto;
import com.ubre.backend.dto.RideDto;
import com.ubre.backend.model.PanicNotification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketNotificationService {

    private static final String PROFILE_CHANGE_TOPIC_PREFIX = "/topic/profile-changes/";
    private static final String RIDE_ASSIGNMENT_TOPIC_PREFIX = "/topic/ride-assignments/";
    private static final String RIDE_REMINDER_TOPIC_PREFIX = "/topic/ride-reminders/";
    private static final String CURRENT_RIDES_TOPIC_PREFIX = "/topic/current-rides/";
    private static final String VEHICLE_LOCATION_TOPIC_PREFIX = "/topic/vehicle-locations";
    private static final String PANIC_TOPIC_PREFIX = "/topic/panic";

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendProfileChangeApproved(Long userId, ProfileChangeNotification notification) {
        messagingTemplate.convertAndSend(PROFILE_CHANGE_TOPIC_PREFIX + userId, notification);
    }

    public void sendProfileChangeRejected(Long userId, ProfileChangeNotification notification) {
        messagingTemplate.convertAndSend(PROFILE_CHANGE_TOPIC_PREFIX + userId, notification);
    }

    // notification for drivers for ride assignments
    public void sendRideAssigned(Long driverId, RideAssignmentNotification notification) {
        messagingTemplate.convertAndSend(RIDE_ASSIGNMENT_TOPIC_PREFIX + driverId, notification);
    }

    // notitication for ride reminder (user receives this notification at scheduled rate)
    public void sendRideReminder(Long userId, RideReminderNotification notification) {
        messagingTemplate.convertAndSend(RIDE_REMINDER_TOPIC_PREFIX + userId, notification);
    }

    // notification for current rides (user receives this notification when ride iS triggered or driver receivres it also)
    public void sendCurrentRideUpdate(Long userId, CurrentRideNotification notification) {
        messagingTemplate.convertAndSend(CURRENT_RIDES_TOPIC_PREFIX + userId, notification);
    }

    // cancel ride with reason from driver
    public void sendRideCancelledToUser(Long userId, CancelNotification notification) {
        messagingTemplate.convertAndSend(CURRENT_RIDES_TOPIC_PREFIX + userId, notification);
    }

    // notification for location of currently active vehicles
    public void sendVehicleLocations(VehicleLocationNotification notification) {
        messagingTemplate.convertAndSend(VEHICLE_LOCATION_TOPIC_PREFIX, notification);
    }

    public void sendPanicNotification(PanicNotification panic) {
        messagingTemplate.convertAndSend(PANIC_TOPIC_PREFIX, panic);
    }
}
