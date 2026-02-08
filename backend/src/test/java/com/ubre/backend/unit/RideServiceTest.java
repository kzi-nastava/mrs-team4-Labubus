package com.ubre.backend.unit;

import com.ubre.backend.dto.RideCardDto;
import com.ubre.backend.dto.RideQueryDto;
import com.ubre.backend.enums.RideStatus;
import com.ubre.backend.repository.DriverRepository;
import com.ubre.backend.repository.PanicRepository;
import com.ubre.backend.repository.RideRepository;
import com.ubre.backend.repository.UserRepository;
import com.ubre.backend.service.EmailService;
import com.ubre.backend.service.RideService;
import com.ubre.backend.service.impl.RideReminderService;
import com.ubre.backend.websocket.WebSocketNotificationService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static spring.data.commons.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RideServiceTest {
    @Mock
    private RideRepository rideRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private WebSocketNotificationService webSocketNotificationService;
    @Mock
    private RideReminderService rideReminderService;
    @Mock
    private PanicRepository panicRepository;
    @Mock
    private EmailService emailService;
    @InjectMocks
    private RideService rideService;

    private final int VALID_PAGE = 0;
    private final int INVALID_Page = -1;
    private final int BIG_PAGE = 231213;

    private final int VALID_COUNT = 4;
    private final int INVALID_COUNT = -1;
    private final int ZERO_COUNT = 0;

    private final RideQueryDto VALID_QUERY = new RideQueryDto(null, "price", false, null);
    private final RideQueryDto DATE_FILTER_QUERY = new RideQueryDto(null, "", false, LocalDateTime.of(2026, 1, 27, 0, 0));
    private final RideQueryDto USER_FILTER_QUERY = new RideQueryDto(33L, "", false, null);
    private final RideQueryDto NULL_QUERY = null;

    @BeforeAll
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void fetching_ride_history_pages() {
        List<RideCardDto> rides = rideService.getRideHistory(VALID_PAGE, VALID_COUNT, VALID_QUERY);
        assertTrue(rides.size() <= VALID_COUNT);

        Mockito.verify(rideRepository).findByStatusIn(List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), any());
    }

    @Test
    public void fetching_ride_history_with_date_filter() {
        List<RideCardDto> rides = rideService.getRideHistory(VALID_PAGE, VALID_COUNT, DATE_FILTER_QUERY);
        assertTrue(rides.size() <= VALID_COUNT);

        for (RideCardDto ride : rides) {
            ride.
            assertTrue(Math.abs(ChronoUnit.DAYS.between(ride, DATE_FILTER_QUERY)));
        }

        Mockito.verify(rideRepository).findByStatusIn(List.of(RideStatus.COMPLETED, RideStatus.CANCELLED), any());
    }

    @Test
    public void fetching_ride_history_with_user_filter() {

    }

    @Test
    public void fetching_ride_history_with_null_filter() {

    }

    @Test
    public void fetching_ride_history_sorted() {

    }

    @Test
    public void fetching_ride_history_with_invalid_page_size() {

    }

    @Test
    public void fetching_ride_history_with_invalid_page_number() {

    }

    @Test
    public void fetching_ride_history_with_big_page_number() {

    }
}
