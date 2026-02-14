package e2e.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisteredUserPage {
    private final WebDriver driver;

    private final By openSideMenu = By.cssSelector("[data-testid='open-side-menu']");
    private final By sideMenuFavourites = By.cssSelector("[data-testid='side-menu-favourites']");
    private final By favoriteRideCardFirst = By.cssSelector("[data-testid='favorite-ride-card-0']");
    private final By reorderRide = By.cssSelector("[data-testid='reorder-ride']");
    private final By destProceed = By.cssSelector("[data-testid='dest-proceed']");
    private final By rideOptionsVehicleStandard = By.cssSelector("[data-testid='ride-options-vehicle-standard']");
    private final By rideOptionsVehicleLuxury = By.cssSelector("[data-testid='ride-options-vehicle-luxury']");
    private final By rideOptionsScheduleRide = By.cssSelector("[data-testid='ride-options-schedule-ride']");
    private final By rideOptionsProceed = By.cssSelector("[data-testid='ride-options-proceed']");
    private final By scheduleTimerHours = By.cssSelector("[data-testid='schedule-timer-hours']");
    private final By scheduleTimerCheckout = By.cssSelector("[data-testid='schedule-timer-checkout']");
    private final By invitePassengersEmail = By.cssSelector("[data-testid='invite-passengers-email']");
    private final By invitePassengersAdd = By.cssSelector("[data-testid='invite-passengers-add']");
    private final By invitePassengersCheckout = By.cssSelector("[data-testid='invite-passengers-checkout']");
    private final By checkoutEstimatedPrice = By.cssSelector("[data-testid='checkout-estimated-price']");
    private final By checkoutConfirmRide = By.cssSelector("[data-testid='checkout-confirm-ride']");
    private final By toastTitle = By.cssSelector("[data-testid='toast-title']");

    public RegisteredUserPage(WebDriver driver) {
        this.driver = driver;
    }

    public void openSideMenu() {
        waitForElementClickable(openSideMenu, 10);
        driver.findElement(openSideMenu).click();
    }

    public void openFavourites() {
        waitForElementClickable(sideMenuFavourites, 10);
        driver.findElement(sideMenuFavourites).click();
    }

    public void waitForFirstFavoriteCard() {
        waitForElementClickable(favoriteRideCardFirst, 10);
    }

    public void openFirstFavoriteRide() {
        waitForElementClickable(favoriteRideCardFirst, 10);
        driver.findElement(favoriteRideCardFirst).click();
    }

    public void waitForReorderRideButton() {
        waitForElementClickable(reorderRide, 10);
    }

    public void reorderRide() {
        waitForElementClickable(reorderRide, 10);
        driver.findElement(reorderRide).click();
    }

    public void proceedFromWaypoints() {
        clickFlowButton(destProceed);
    }

    public void selectStandardVehicle() {
        clickFlowButton(rideOptionsVehicleStandard);
    }

    public void selectLuxuryVehicle() {
        clickFlowButton(rideOptionsVehicleLuxury);
    }

    public void proceedRideOptions() {
        clickFlowButton(rideOptionsProceed);
    }

    public void openScheduleRide() {
        clickFlowButton(rideOptionsScheduleRide);
    }

    public void increaseScheduleHours(int times) {
        waitForElementClickable(scheduleTimerHours, 10);
        for (int i = 0; i < times; i++) {
            clickFlowButton(scheduleTimerHours);
        }
    }

    public void confirmScheduleTime() {
        clickFlowButton(scheduleTimerCheckout);
    }

    public void waitForPassengersSection() {
        waitForElementClickable(invitePassengersEmail, 10);
    }

    public void addPassengers(String... emails) {
        waitForPassengersSection();
        WebElement input = driver.findElement(invitePassengersEmail);
        for (String email : emails) {
            input.clear();
            input.sendKeys(email);
            clickFlowButton(invitePassengersAdd);
        }
    }

    public void skipPassengers() {
        clickFlowButton(invitePassengersCheckout);
    }

    public void proceedFromPassengers() {
        clickFlowButton(invitePassengersCheckout);
    }

    public String waitForEstimatedPrice() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        return wait.until(d -> {
            String text = d.findElement(checkoutEstimatedPrice).getText();
            if (text == null) {
                return null;
            }
            String trimmed = text.trim();
            if (trimmed.isEmpty() || "null".equalsIgnoreCase(trimmed)) {
                return null;
            }
            return trimmed;
        });
    }

    public void confirmRide() {
        clickFlowButton(checkoutConfirmRide);
    }

    public String waitForToastTitle() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        return wait.until(d -> {
            String text = d.findElement(toastTitle).getText();
            if (text == null) {
                return null;
            }
            String trimmed = text.trim();
            return trimmed.isEmpty() ? null : trimmed;
        });
    }

    private void waitForElementClickable(By selector, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
        wait.until(ExpectedConditions.elementToBeClickable(selector));
    }

    /** Waits for element then clicks via JavaScript to avoid "element click intercepted" from overlays/z-index. */
    private void clickFlowButton(By selector) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(selector));
        wait.until(ExpectedConditions.visibilityOf(el));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
    }
}
