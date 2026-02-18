package e2e.tests;

import e2e.config.Config;
import e2e.core.BaseTest;
import e2e.pages.GuestPage;
import e2e.pages.LoginPage;
import java.time.Duration;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.openqa.selenium.support.ui.WebDriverWait;

public class UserPageTest extends BaseTest {

    @Test
    public void userCanLoginFromGuestPage() {
        driver.get(Config.BASE_URL);

        GuestPage guestPage = new GuestPage(driver);
        Assert.assertTrue(guestPage.isDisplayed(), "Guest page is not displayed");

        guestPage.openSideMenu();
        LoginPage loginPage = guestPage.goToLogin();
        loginPage.login("test@uber.com", "admin123");

        GuestPage userPage = new GuestPage(driver);
        userPage.openSideMenu();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        String userName = wait.until(d -> {
            String text = userPage.getSideMenuUserNameText();
            if (text == null) {
                return null;
            }
            String trimmed = text.trim();
            if (trimmed.isEmpty() || "Guest".equalsIgnoreCase(trimmed)) {
                return null;
            }
            return trimmed;
        });

        Assert.assertTrue(!userName.isEmpty(), "User name should not be empty after login");
        Assert.assertNotEquals(userName, "Guest", "User name should not be 'Guest' after login");
    }
}
