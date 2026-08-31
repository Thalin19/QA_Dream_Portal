package tests;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.HomePage;

import java.util.Set;

public class HomePageTest extends BaseTest {

    @Test(description = "Loading animation should be visible immediately, then disappear")
    public void testLoadingAnimationAppearsAndDisappears() {
        HomePage homePage = new HomePage(driver);
        homePage.open(BASE_URL);

        // Right after load, the spinner should be showing
        Assert.assertTrue(homePage.isLoadingAnimationDisplayed(),
                "Loading animation should be visible right after page load");

        // Wait for it to go away (script.js hides it after 3 seconds)
        homePage.waitForLoadingToDisappear();
        Assert.assertFalse(homePage.isLoadingAnimationDisplayed(),
                "Loading animation should disappear after ~3 seconds");
    }

    @Test(description = "Main content and My Dreams button should become visible after loading")
    public void testMainContentAndButtonBecomeVisible() {
        HomePage homePage = new HomePage(driver);
        homePage.open(BASE_URL);

        homePage.waitForMainContentVisible();

        Assert.assertTrue(homePage.isMainContentVisible(),
                "Main content should be visible after loading finishes");
        Assert.assertTrue(homePage.isDreamButtonVisible(),
                "'My Dreams' button should be visible after loading finishes");
    }

    @Test(description = "Clicking 'My Dreams' should open dreams-diary.html and dreams-total.html in new tabs")
    public void testMyDreamsButtonOpensTwoNewTabs() {
        HomePage homePage = new HomePage(driver);
        homePage.open(BASE_URL);
        homePage.waitForMainContentVisible();

        String originalWindow = driver.getWindowHandle();

        homePage.clickDreamButton();

        // Wait until 3 windows/tabs are open (original + 2 new ones)
        Assert.assertEquals(driver.getWindowHandles().size(), 3,
                "Expected the original tab plus 2 new tabs to be open");

        // Check the URLs of the new tabs to confirm the RIGHT pages opened
        boolean foundDiary = false;
        boolean foundTotal = false;

        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(originalWindow)) {
                driver.switchTo().window(handle);
                String url = driver.getCurrentUrl();
                if (url.contains("dreams-diary.html")) foundDiary = true;
                if (url.contains("dreams-total.html")) foundTotal = true;
            }
        }

        Assert.assertTrue(foundDiary, "dreams-diary.html should have opened in a new tab");
        Assert.assertTrue(foundTotal, "dreams-total.html should have opened in a new tab");

        driver.switchTo().window(originalWindow);
    }
}
