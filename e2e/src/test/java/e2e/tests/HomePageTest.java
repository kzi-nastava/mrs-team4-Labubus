package e2e.tests;

import e2e.config.Config;
import e2e.core.BaseTest;
import e2e.pages.GuestPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends BaseTest {

    @Test
    public void homePageIsDisplayed() {
        driver.get(Config.BASE_URL);
        GuestPage page = new GuestPage(driver);
        Assert.assertTrue(page.isDisplayed(), "Home page is not displayed");
        // wait for 10 seconds
        try { Thread.sleep(10000); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
