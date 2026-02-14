package e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {
    private final WebDriver driver;

    private final By emailInput = By.cssSelector("[data-testid='login-email']");
    private final By passwordInput = By.cssSelector("[data-testid='login-password']");
    private final By submitButton = By.cssSelector("[data-testid='login-submit']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean isDisplayed() {
        return driver.findElement(emailInput).isDisplayed()
                && driver.findElement(passwordInput).isDisplayed()
                && driver.findElement(submitButton).isDisplayed();
    }

    public void setEmail(String email) {
        WebElement input = driver.findElement(emailInput);
        input.clear();
        input.sendKeys(email);
    }

    public void setPassword(String password) {
        WebElement input = driver.findElement(passwordInput);
        input.clear();
        input.sendKeys(password);
    }

    public void submit() {
        driver.findElement(submitButton).click();
    }

    public void login(String email, String password) {
        setEmail(email);
        setPassword(password);
        submit();
    }
}
