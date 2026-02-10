package com.ubre.backend.repository;

import com.ubre.backend.enums.UserStatus;
import com.ubre.backend.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.sql.DataSource;
import java.util.UUID;

/*
Ovi repository testovi pokrivaju sve custom (ne-JPA) metode koje Student 1
koristi u orderRide logici.

Testiramo samo:
- existsDriverWithActiveStatus()
- areAllDriversOnRideWithPendingRides()

Razlog:
Repozitorijum sadrži COUNT(*) upite nad celom bazom.
Pošto test baza NIJE prazna (sadrži postojeće podatke),
mogu se pouzdano testirati samo scenariji koji ne zavise
od kompletnog sadržaja baze.

Zbog toga:
- existsDriverWithActiveStatus() testiramo samo u true scenariju
- areAllDriversOnRideWithPendingRides() testiramo samo u false scenariju

JPA-izvedene metode (findById, save, findByStatus, itd.)
nisu predmet testiranja prema specifikaciji.
*/


@DataJpaTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RideOrderTest extends AbstractTestNGSpringContextTests {
    private static final String TEST_EMAIL_PREFIX = "repo-test-" + UUID.randomUUID();

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    @BeforeClass
    public void verifyTestDatabase() throws Exception {
        try (var connection = dataSource.getConnection()) {
            String url = connection.getMetaData().getURL();
            boolean isTestDb = url != null && (url.contains("_test") || url.contains("ubredb_test"));
            if (!isTestDb) {
                Assert.fail("Refusing to run repository tests on non-test database. URL: " + url);
            }
        }
    }

    @Test
    public void existsDriverWithActiveStatus_active_returnsTrue() {
        Driver active = persistDriver(UserStatus.ACTIVE);
        Assert.assertNotNull(active.getId());

        Boolean result = driverRepository.existsDriverWithActiveStatus();
        Assert.assertTrue(result);
    }

    @Test
    public void areAllDriversOnRideWithPendingRides_oneDriverNotBusy_returnsFalse() {
        Driver idleDriver = persistDriver(UserStatus.ACTIVE);
        Assert.assertNotNull(idleDriver.getId());

        Boolean result = driverRepository.areAllDriversOnRideWithPendingRides();
        Assert.assertFalse(result);
    }

    private Driver persistDriver(UserStatus status) {
        Driver driver = new Driver();
        driver.setEmail(uniqueEmail("driver"));
        driver.setPassword("pass123!");
        driver.setName("Test");
        driver.setSurname("Driver");
        driver.setAddress("Address 2");
        driver.setPhone("200-300");
        driver.setStatus(status);
        entityManager.persist(driver);
        entityManager.flush();
        return driver;
    }

    private String uniqueEmail(String prefix) {
        return TEST_EMAIL_PREFIX + "-" + prefix + "-" + UUID.randomUUID() + "@test.local";
    }
}


