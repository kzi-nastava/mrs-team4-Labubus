package e2e.tests;

import e2e.config.Config;
import e2e.core.BaseTest;
import e2e.pages.GuestPage;
import e2e.pages.LoginPage;
import e2e.pages.RegisteredUserPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RideReviewTest extends BaseTest {
    private RegisteredUserPage userPage;

    @BeforeMethod
    public void testPreparation() {
        driver.get(Config.BASE_URL);

        GuestPage guestPage = new GuestPage(driver);
        Assert.assertTrue(guestPage.isDisplayed(), "Guest page is not displayed");

        guestPage.openSideMenu();
        LoginPage loginPage = guestPage.goToLogin();
        loginPage.login("test@uber.com", "admin123");

        userPage = new RegisteredUserPage(driver);
        userPage.openSideMenu();
        userPage.openRideHistory();
    }

    @Test
    public void sendReviewWithNoText() {
        userPage.openNthHistoryRide(1);
        userPage.openReviewModalForSelectedRide();
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal is not open");

        userPage.setReviewRating(4);
        userPage.setReviewText("");
        userPage.sendReview();
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal submission succeeded");
        Assert.assertTrue(userPage.isReviewTextInputErrorDisplayed(), "Review text input does not display validation error");
    }

    @Test
    public void sendValidReview() {
        userPage.openFirstHistoryRide();
        userPage.openReviewModalForSelectedRide();
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal is not open");

        userPage.setReviewRating(4);
        userPage.setReviewText("Test review");
        userPage.sendReview();
        Assert.assertFalse(userPage.isReviewModalDisplayed(), "Review modal is open");
    }

    @Test
    public void reviewOlderRide() {
        userPage.openNthHistoryRide(25);
        userPage.openReviewModalForSelectedRide();
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal is not open");

        userPage.setReviewRating(4);
        userPage.setReviewText("Test review");
        userPage.sendReview();
        Assert.assertEquals(userPage.waitForToastTitle(), "Error");
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal submission succeeded");
    }

    @Test(dependsOnMethods = {"sendValidReview"})
    public void reviewAlreadyReviewedRide() {
        userPage.openFirstHistoryRide();
        userPage.openReviewModalForSelectedRide();
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal is not open");

        userPage.setReviewRating(4);
        userPage.setReviewText("Test review");
        userPage.sendReview();
        Assert.assertEquals(userPage.waitForToastTitle(), "Error");
        Assert.assertTrue(userPage.isReviewModalDisplayed(), "Review modal submission succeeded");
    }
}
