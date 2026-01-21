import com.ubre.backend.websocket.WebSocketNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

@Service
public class RideReminderService {

    @Autowired
    private final ThreadPoolTaskScheduler scheduler;
    @Autowired
    private final WebSocketNotificationService webSocketNotificationService;

    public void start(long userId, long rideId, Instant startTime) {
        scheduleNext(userId, rideId, startTime);
    }

    private void scheduleNext(long userId, long rideId, Instant startTime) {
        long minutesLeft = Duration.between(Instant.now(), startTime).toMinutes();

        if (minutesLeft <= 0) return; // STOP

        // pošalji notifikaciju
        ws.convertAndSend(
                "/topic/notifications/" + userId,
                Map.of("type", "RIDE_REMINDER", "rideId", rideId, "minutesLeft", minutesLeft)
        );

        // odredi sledeći interval
        long nextDelayMinutes = minutesLeft > 15 ? 15 : 5;

        scheduler.schedule(
                () -> scheduleNext(userId, rideId, startTime),
                Instant.now().plus(Duration.ofMinutes(nextDelayMinutes))
        );
    }
}
