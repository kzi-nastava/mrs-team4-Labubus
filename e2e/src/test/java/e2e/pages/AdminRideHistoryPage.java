package e2e.pages;

import java.time.Duration;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminRideHistoryPage {
    private final WebDriver driver;

    private final By rideCards = By.cssSelector(".ride-card");
    private final By noRidesFound = By.cssSelector("[data-testId='no-rides-found']");
    private final By filterDateInput = By.cssSelector("[data-testId='filter-date'], input[type='datetime-local']");
    private final By sortDropdownToggle = By.cssSelector("[data-testId='sort-dropdown-toggle'], .drop-down");
    private final By sortDirectionToggle = By.cssSelector("[data-testId='sort-direction-toggle'], .sort-direction");


    public AdminRideHistoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForRideCards() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(rideCards));
    }

    public void filterByDate(String userDate) {
        WebElement dateInput = driver.findElement(filterDateInput);
        dateInput.clear();
        dateInput.sendKeys(userDate);
        dateInput.sendKeys(Keys.TAB);
        dateInput.sendKeys("03:33");
        dateInput.sendKeys(Keys.ARROW_UP);
        dateInput.sendKeys(Keys.ENTER);
    }

    public int getRideCardCount() {
        waitForRideCards();
        return driver.findElements(rideCards).size();
    }

    public String getFirstRideDate() {
        WebElement firstCard = driver.findElements(rideCards).get(0);
        return firstCard.findElement(By.cssSelector("[data-testId='ride-start-time']")).getText();
    }

    public String getSecondRideDate() {
        WebElement firstCard = driver.findElements(rideCards).get(1);
        return firstCard.findElement(By.cssSelector("[data-testId='ride-start-time']")).getText();
    }

    public boolean isNoRidesFoundDisplayed() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(ExpectedConditions.visibilityOfElementLocated(noRidesFound))
                .isDisplayed();
    }

    public void selectSortBy(String field) {
        driver.findElement(sortDropdownToggle).click();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".fieldSelection")
        ));

        String xpath = String.format("//div[@class='field' and text()='%s']", field);
        WebElement option = driver.findElement(By.xpath(xpath));
        option.click();

        waitForRideCards();
    }

    public boolean hasRideCards() {
        return driver.findElements(rideCards).size() > 0;
    }

    public void toggleSortDirection() {
        WebElement toggle = driver.findElement(sortDirectionToggle);
        toggle.click();
        waitForRideCards();
    }
}