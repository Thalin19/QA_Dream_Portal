package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents dreams-total.html.
 *
 * Actual HTML (confirmed from source):
 *   <table id="dreamsTotal">
 *     <tbody>
 *       <tr><td>Good Dreams</td><td>6</td></tr>
 *       <tr><td>Bad Dreams</td><td>4</td></tr>
 *       <tr><td>Total Dreams</td><td>10</td></tr>
 *       <tr><td>Dreams This Week</td><td>7</td></tr>
 *       <tr><td>Recurring Dreams</td><td>2</td></tr>
 *     </tbody>
 *   </table>
 *
 * Rather than one getter per stat, we read the table into a Map so the
 * test can just do getStat("Good Dreams") - cleaner and less repetitive.
 */
public class DreamsTotalPage extends BasePage {

    private final By tableRows = By.cssSelector("#dreamsTotal tbody tr");

    public DreamsTotalPage(WebDriver driver) {
        super(driver);
    }

    public Map<String, Integer> getStats() {
        Map<String, Integer> stats = new HashMap<>();
        for (WebElement tr : driver.findElements(tableRows)) {
            List<WebElement> cells = tr.findElements(By.tagName("td"));
            String label = cells.get(0).getText().trim();
            int value = Integer.parseInt(cells.get(1).getText().trim());
            stats.put(label, value);
        }
        return stats;
    }

    public int getStat(String label) {
        return getStats().get(label);
    }
}
