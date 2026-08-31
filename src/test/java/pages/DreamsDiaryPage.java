package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents dreams-diary.html.
 *
 * Actual HTML (confirmed from source):
 *   <table id="dreamsDiary">
 *     <tbody>
 *       <tr><td>Flying over mountains</td><td>1 day ago</td><td>Good</td></tr>
 *       ... 10 rows total ...
 *     </tbody>
 *   </table>
 */
public class DreamsDiaryPage extends BasePage {

    private final By tableRows = By.cssSelector("#dreamsDiary tbody tr");

    public DreamsDiaryPage(WebDriver driver) {
        super(driver);
    }

    public int getDreamCount() {
        return driver.findElements(tableRows).size();
    }

    /** Simple record-like holder for one row's data. */
    public static class DreamRow {
        public final String name;
        public final String daysAgo;
        public final String type;

        public DreamRow(String name, String daysAgo, String type) {
            this.name = name;
            this.daysAgo = daysAgo;
            this.type = type;
        }
    }

    public List<DreamRow> getAllDreamRows() {
        List<DreamRow> rows = new ArrayList<>();
        for (WebElement tr : driver.findElements(tableRows)) {
            List<WebElement> cells = tr.findElements(By.tagName("td"));
            rows.add(new DreamRow(
                    cells.get(0).getText(),
                    cells.get(1).getText(),
                    cells.get(2).getText()
            ));
        }
        return rows;
    }

    public List<String> getAllDreamTypes() {
        List<String> types = new ArrayList<>();
        for (DreamRow row : getAllDreamRows()) {
            types.add(row.type);
        }
        return types;
    }

    public List<String> getAllDreamNames() {
        List<String> names = new ArrayList<>();
        for (DreamRow row : getAllDreamRows()) {
            names.add(row.name);
        }
        return names;
    }
}
