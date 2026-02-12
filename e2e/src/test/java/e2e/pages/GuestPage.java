package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class GuestPage {

//    WebElement openMenu = driver.findElement(By.cssSelector("[data-testid='open-side-menu']"));
//    WebElement email = driver.findElement(By.cssSelector("[data-testid='login-email']"));
//    WebElement password = driver.findElement(By.cssSelector("[data-testid='login-password']"));
//    WebElement submit = driver.findElement(By.cssSelector("[data-testid='login-submit']"));
//    WebElement sideMenuLogin = driver.findElement(By.cssSelector("[data-testid='side-menu-login']"));

    private final WebDriver driver;

    public GuestPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return driver.findElement(By.tagName("body")).isDisplayed();
    }
}
