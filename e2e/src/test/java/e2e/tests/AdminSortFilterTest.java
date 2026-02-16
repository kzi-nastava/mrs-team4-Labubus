package e2e.tests;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import e2e.config.Config;
import e2e.core.BaseTest;
import e2e.pages.AdminRideHistoryPage;
import e2e.pages.GuestPage;
import e2e.pages.LoginPage;
import e2e.pages.RegisteredUserPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminSortFilterTest extends BaseTest {

    @Test
    public void adminCanAccessRideHistory() {
        loginAndOpenRideHistory();

        AdminRideHistoryPage historyPage = new AdminRideHistoryPage(driver);
        historyPage.waitForRideCards();

        int count = historyPage.getRideCardCount();
        Assert.assertTrue(count > 0, "Should have rides before filtering");

    }

    @Test
    public void adminCanFilterByDate() {
        loginAndOpenRideHistory();

        AdminRideHistoryPage historyPage = new AdminRideHistoryPage(driver);
        historyPage.waitForRideCards();

        historyPage.filterByDate("01/20/2026");
        int count = historyPage.getRideCardCount();
        historyPage.waitForRideCards();
        Assert.assertTrue(count > 0, "Should have rides on that date");
    }

    @Test
    public void returnsNoRideFoundForFutureDate() {
        loginAndOpenRideHistory();

        AdminRideHistoryPage historyPage = new AdminRideHistoryPage(driver);
        historyPage.waitForRideCards();

        historyPage.filterByDate("03/30/2026");

        Assert.assertTrue(historyPage.isNoRidesFoundDisplayed(),
                "Expected 'No rides found' message for future date");
    }

    @Test
    public void adminCanSortByStartTimeAscDesc() {
        loginAndOpenRideHistory();

        AdminRideHistoryPage historyPage = new AdminRideHistoryPage(driver);
        historyPage.waitForRideCards();

        historyPage.selectSortBy("Start time");
        String firstCardTimeAsc = historyPage.getFirstRideDate();
        String secondCardTimeAsc = historyPage.getSecondRideDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM yyyy h:mm a");

        LocalDateTime firstAsc = LocalDateTime.parse(firstCardTimeAsc, formatter);
        LocalDateTime secondAsc = LocalDateTime.parse(secondCardTimeAsc, formatter);

        Assert.assertTrue(firstAsc.isAfter(secondAsc) || firstAsc.isEqual(secondAsc),
                "Should be sorted by newer first");

        historyPage.toggleSortDirection();

        String firstCardTimeDesc = historyPage.getFirstRideDate();
        String secondCardTimeDesc = historyPage.getSecondRideDate();


        LocalDateTime firstDesc = LocalDateTime.parse(firstCardTimeDesc, formatter);
        LocalDateTime secondDesc = LocalDateTime.parse(secondCardTimeDesc, formatter);

        Assert.assertTrue(firstDesc.isBefore(secondDesc) || firstDesc.isEqual(firstAsc),
                "Should be sorted by older first");

        Assert.assertTrue(firstDesc.isBefore(firstAsc), "Should be sorted by desc");
    }

    @Test
    public void adminCanSelectDifferentSortOptions() {
        loginAndOpenRideHistory();

        AdminRideHistoryPage historyPage = new AdminRideHistoryPage(driver);
        historyPage.waitForRideCards();

        String[] sortOptions = {"Start time", "End time", "Price", "Distance", "Driver"};

        for (String option : sortOptions) {
            historyPage.selectSortBy(option);

            Assert.assertTrue(historyPage.hasRideCards(),
                    "Rides should still be visible after sorting by " + option);
        }
    }

    public void loginAndOpenRideHistory() {
        driver.get(Config.BASE_URL);

        GuestPage guestPage = new GuestPage(driver);
        guestPage.openSideMenu();
        LoginPage loginPage = guestPage.goToLogin();
        loginPage.login("admin1@ubre.com", "admin1234");

        RegisteredUserPage userPage = new RegisteredUserPage(driver);
        userPage.openSideMenu();
        userPage.openRideHistory();
    }
}

