package e2e.pages;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.tools.Diagnostic;

public class RegisteredUserPage {
    private final WebDriver driver;

    private final By openSideMenu = By.cssSelector("[data-testid='open-side-menu']");
    private final By sideMenuFavourites = By.cssSelector("[data-testid='side-menu-favourites']");
    private final By sideMenuRideHistory = By.cssSelector("[data-testid='side-menu-ride-history']");
    private final By favoriteRideCardFirst = By.cssSelector("[data-testid='favorite-ride-card-0']");
    private final By historyRideCardFirst = By.cssSelector("[data-testid='history-ride-card-0']");
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
    private final By reviewIcon = By.cssSelector("[data-testId='history-ride-details-driver-action']");
    private final By reviewModalBackdrop = By.cssSelector("[data-testId='review-backdrop']");
    private final By reviewTextInput = By.cssSelector("[data-testId='review-text-input']");
    private final By reviewSendButton = By.cssSelector("[data-testId='review-send']");
    private final By reviewCancelButton = By.cssSelector("[data-testId='review-cancel']");

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

    public void openRideHistory() {
        waitForElementClickable(sideMenuRideHistory, 10);
        driver.findElement(sideMenuRideHistory).click();
    }

    public void waitForFirstFavoriteCard() {
        waitForElementClickable(favoriteRideCardFirst, 10);
    }

    public void waitForFirstHistoryCard() {
        waitForElementClickable(historyRideCardFirst, 10);
    }

    public void waitForNthHistoryCard(int cardIndex) {
        WebElement firstCard = driver.findElement(historyRideCardFirst);
        WebElement cardList = firstCard.findElement(By.xpath("../.."));
        List<WebElement> cards = cardList.findElements(By.xpath("./*"));
        while(cards.size() <= cardIndex) {
            // Scroll the ride card list to load more
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", cardList);

            // Wait for the next card to be fetched
            By historyRideNextCard = By.cssSelector(String.format("[data-testid='history-ride-card-%d']", cards.size()));
            waitForElementClickable(historyRideNextCard, 10);

            // Update the cards and check if the target card is fetched
            List<WebElement> newCards = cardList.findElements(By.xpath("./*"));
            if (newCards.size() == cards.size())
                break;
            cards = newCards;
        }

        By historyRideCardNth = By.cssSelector(String.format("[data-testid='history-ride-card-%d']", cardIndex));
        waitForElementClickable(historyRideCardNth, 10);
    }

    public void openFirstFavoriteRide() {
        waitForElementClickable(favoriteRideCardFirst, 10);
        driver.findElement(favoriteRideCardFirst).click();
    }

    public void openFirstHistoryRide() {
        waitForElementClickable(historyRideCardFirst, 10);
        driver.findElement(historyRideCardFirst).click();
    }

    public void openNthHistoryRide(int cardIndex) {
        WebElement firstCard = driver.findElement(historyRideCardFirst);
        WebElement cardList = firstCard.findElement(By.xpath("../.."));
        List<WebElement> cards = cardList.findElements(By.xpath("./*"));
        while(cards.size() <= cardIndex) {
            // Scroll the ride card list to load more
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].scrollTop = arguments[0].scrollHeight", cardList);

            // Wait for the next card to be fetched
            By historyRideNextCard = By.cssSelector(String.format("[data-testid='history-ride-card-%d']", cards.size()));
            waitForElementClickable(historyRideNextCard, 10);

            // Update the cards and check if the target card is fetched
            List<WebElement> newCards = cardList.findElements(By.xpath("./*"));
            if (newCards.size() == cards.size())
                break;
            cards = newCards;
        }

        By historyRideCardNth = By.cssSelector(String.format("[data-testid='history-ride-card-%d']", cardIndex));
        waitForElementClickable(historyRideCardNth, 10);
        driver.findElement(historyRideCardNth).click();
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

    public void openReviewModalForSelectedRide() {
        waitForElementClickable(reviewIcon, 10);
        driver.findElement(reviewIcon).click();
    }

    public boolean isReviewModalDisplayed() {
        return !Arrays.asList(driver.findElement(reviewModalBackdrop).getAttribute("class").split("\\s+")).contains("fade");
    }

    public boolean isReviewTextInputErrorDisplayed() {
        return Arrays.asList(driver.findElement(reviewTextInput).getAttribute("class").split("\\s+")).contains("error");
    }

    public void setReviewRating(int rating) {
        By reviewRatingStar = By.cssSelector(String.format("[data-testId='review-start-%d']", rating - 1));
        waitForElementClickable(reviewRatingStar, 10);
        driver.findElement(reviewRatingStar).click();
    }

    public void setReviewText(String text) {
        waitForElementClickable(reviewTextInput, 10);
        driver.findElement(reviewTextInput).sendKeys(text);
    }

    public void sendReview() {
        waitForElementClickable(reviewSendButton, 10);
        driver.findElement(reviewSendButton).click();
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> {
            boolean notificationDisplayed = d.findElements(toastTitle).stream().findFirst().map(WebElement::getText).map(text -> !text.isBlank()).orElse(false);
            boolean modalClosed = d.findElements(reviewModalBackdrop).stream().findFirst().map(e -> e.getAttribute("class")).map(classes -> classes.contains("fade")).orElse(false);
            boolean errorDisplayed = d.findElements(reviewTextInput).stream().findFirst().map(e -> e.getAttribute("class")).map(classes -> classes.contains("error")).orElse(false);
            return notificationDisplayed || modalClosed || errorDisplayed;
        });
    }

    public void closeReview() {
        waitForElementClickable(reviewCancelButton, 10);
        driver.findElement(reviewCancelButton).click();
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
