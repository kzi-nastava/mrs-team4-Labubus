package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GuestPage {
    private final WebDriver driver;
    private final By openMenuButton = By.cssSelector("[data-testid='open-side-menu']");
    private final By sideMenuLoginButton = By.cssSelector("[data-testid='side-menu-login']");
    private final By sideMenuUserName = By.cssSelector("[data-testid='side-menu-user-name']");

    public GuestPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return driver.findElement(By.tagName("body")).isDisplayed();
    }

    public boolean isSideMenuButtonDisplayed() {
        return driver.findElement(openMenuButton).isDisplayed();
    }

    public void openSideMenu() {
        driver.findElement(openMenuButton).click();
    }

    public LoginPage goToLogin() {
        driver.findElement(sideMenuLoginButton).click();
        return new LoginPage(driver);
    }

    public String getSideMenuUserNameText() {
        return driver.findElement(sideMenuUserName).getText();
    }
}
