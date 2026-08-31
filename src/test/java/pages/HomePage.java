package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Represents index.html - the Dream Portal home page.
 *
 * Actual HTML (confirmed from source):
 *   <div class="loading-animation" id="loadingAnimation"> ... </div>
 *   <div class="main-content hidden" id="mainContent"> ... </div>
 *   <button id="dreamButton" class="hidden">My Dreams</button>
 *
 * script.js waits 3000ms (3 seconds), then:
 *   - adds "hidden" class to #loadingAnimation
 *   - removes "hidden" class from #mainContent
 *   - removes "hidden" class from #dreamButton
 *   - clicking #dreamButton opens dreams-diary.html AND dreams-total.html
 *     each in a new tab via window.open(url, '_blank')
 */
public class HomePage extends BasePage {

    private final By loadingAnimation = By.id("loadingAnimation");
    private final By mainContent = By.id("mainContent");
    private final By dreamButton = By.id("dreamButton");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
    }

    /** True if the loading spinner is currently visible (right after page load). */
    public boolean isLoadingAnimationDisplayed() {
        WebElement el = driver.findElement(loadingAnimation);
        // it's visible if it does NOT have the "hidden" class
        return el.isDisplayed() && !el.getAttribute("class").contains("hidden");
    }

    /** Waits until the loading animation has the "hidden" class applied (i.e. it disappeared). */
    public void waitForLoadingToDisappear() {
        wait.until(d -> {
            WebElement el = d.findElement(loadingAnimation);
            return el.getAttribute("class").contains("hidden");
        });
    }

    /** Waits until the main content no longer has the "hidden" class. */
    public void waitForMainContentVisible() {
        wait.until(d -> {
            WebElement el = d.findElement(mainContent);
            return !el.getAttribute("class").contains("hidden");
        });
    }

    public boolean isMainContentVisible() {
        WebElement el = driver.findElement(mainContent);
        return !el.getAttribute("class").contains("hidden");
    }

    public boolean isDreamButtonVisible() {
        WebElement el = driver.findElement(dreamButton);
        return !el.getAttribute("class").contains("hidden");
    }

    public void clickDreamButton() {
        WebElement btn = waitForClickable(dreamButton);
        btn.click();
    }
}
