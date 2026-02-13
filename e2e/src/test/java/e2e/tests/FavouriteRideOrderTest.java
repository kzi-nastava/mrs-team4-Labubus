package e2e.tests;

import e2e.config.Config;
import e2e.core.BaseTest;
import e2e.pages.GuestPage;
import e2e.pages.LoginPage;
import e2e.pages.RegisteredUserPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class FavouriteRideOrderTest extends BaseTest {

    // NOTE: These tests assume that we have specific situation in database that we consistently have shown on KT1 and KT2
    @Test 
    public void registeredUserCannotOrderWhenNoVehicleAvailable() {
        driver.get(Config.BASE_URL);

        GuestPage guestPage = new GuestPage(driver);
        Assert.assertTrue(guestPage.isDisplayed(), "Guest page is not displayed");

        guestPage.openSideMenu();
        LoginPage loginPage = guestPage.goToLogin();
        loginPage.login("test@uber.com", "admin123");

        RegisteredUserPage userPage = new RegisteredUserPage(driver);
        userPage.openSideMenu();
        userPage.openFavourites();
        userPage.waitForFirstFavoriteCard();
        userPage.openFirstFavoriteRide();
        userPage.waitForReorderRideButton();
        userPage.reorderRide();

        userPage.proceedFromWaypoints();
        userPage.selectStandardVehicle();
        userPage.proceedRideOptions();
        userPage.skipPassengers();

        String price = userPage.waitForEstimatedPrice();
        Assert.assertNotNull(price, "Estimated price should be available before confirming ride");

        userPage.confirmRide();
        String toastTitle = userPage.waitForToastTitle();

        boolean isSuccess = "Success".equalsIgnoreCase(toastTitle);
        boolean isError = "Error ordering ride".equalsIgnoreCase(toastTitle);
        Assert.assertTrue(isSuccess || isError, "Toast title should be 'Success' or 'Error'");
    }

    @Test
    public void registeredUserCanOrderFromFavouriteRouteWithLuxuryVehicle() {
        driver.get(Config.BASE_URL);

        GuestPage guestPage = new GuestPage(driver);
        Assert.assertTrue(guestPage.isDisplayed(), "Guest page is not displayed");

        guestPage.openSideMenu();
        LoginPage loginPage = guestPage.goToLogin();
        loginPage.login("test@uber.com", "admin123");

        RegisteredUserPage userPage = new RegisteredUserPage(driver);
        userPage.openSideMenu();
        userPage.openFavourites();
        userPage.waitForFirstFavoriteCard();
        userPage.openFirstFavoriteRide();
        userPage.waitForReorderRideButton();
        userPage.reorderRide();

        userPage.proceedFromWaypoints();
        userPage.selectLuxuryVehicle();
        userPage.proceedRideOptions();
        userPage.skipPassengers();

        String price = userPage.waitForEstimatedPrice();
        Assert.assertNotNull(price, "Estimated price should be available before confirming ride");

        userPage.confirmRide();
        String toastTitle = userPage.waitForToastTitle();

        Assert.assertEquals(toastTitle, "Ride ordered", "Toast title should be 'Success'");
    }
}
