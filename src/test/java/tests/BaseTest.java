package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

/**
 * Every test class extends this. It opens a fresh Chrome browser before
 * EACH @Test method, and closes it after. That way tests don't interfere
 * with each other (a very important testing principle: tests should be
 * independent and repeatable).
 */
public class BaseTest {

    protected WebDriver driver;
    protected static final String BASE_URL = "https://arjitnigam.github.io/myDreams/";
    private static final String SCREENSHOT_DIR = "screenshots";

    @BeforeMethod
    public void setUp() {
        // WebDriverManager automatically downloads the matching chromedriver
        // for whatever Chrome version is installed on this machine.
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // Uncomment the line below to run without opening a visible browser window
        // (useful later for CI/CD pipelines):
        // options.addArguments("--headless=new");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
    }

    /**
     * TestNG passes in the result of the test that just ran, INCLUDING whether
     * it passed or failed. We check that here, BEFORE quitting the driver
     * (the screenshot has to happen while the browser is still open).
     */
    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result.getName());
        }
        if (driver != null) {
            driver.quit();
        }
    }

    private void takeScreenshot(String testName) {
        try {
            Files.createDirectories(Paths.get(SCREENSHOT_DIR));

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String fileName = testName + "_" + timestamp + ".png";
            Path destination = Paths.get(SCREENSHOT_DIR, fileName);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), destination);

            System.out.println("Screenshot saved for failed test '" + testName + "' -> " + destination);
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot for '" + testName + "': " + e.getMessage());
        }
    }
}
