package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DreamsDiaryPage;
import pages.DreamsTotalPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DreamsTotalTest extends BaseTest {

    @Test(description = "Summary stats (Good/Bad/Total/Recurring) should match expected values")
    public void testSummaryStatsAreCorrect() {
        DreamsTotalPage totalPage = new DreamsTotalPage(driver);
        totalPage.open(BASE_URL + "dreams-total.html");

        Map<String, Integer> stats = totalPage.getStats();

        Assert.assertEquals((int) stats.get("Good Dreams"), 6, "Good Dreams count mismatch");
        Assert.assertEquals((int) stats.get("Bad Dreams"), 4, "Bad Dreams count mismatch");
        Assert.assertEquals((int) stats.get("Total Dreams"), 10, "Total Dreams count mismatch");
        Assert.assertEquals((int) stats.get("Recurring Dreams"), 2, "Recurring Dreams count mismatch");
    }

    @Test(description = "'Flying over mountains' and 'Lost in maze' should be the recurring dreams, " +
            "verified by cross-checking the diary page's actual dream names")
    public void testRecurringDreamsMatchDiaryData() {
        // Step 1: get the real dream names from the diary page
        DreamsDiaryPage diaryPage = new DreamsDiaryPage(driver);
        diaryPage.open(BASE_URL + "dreams-diary.html");
        List<String> names = diaryPage.getAllDreamNames();

        // Step 2: count how many times each name appears
        Map<String, Integer> occurrences = new HashMap<>();
        for (String name : names) {
            occurrences.merge(name, 1, Integer::sum);
        }

        // Step 3: a dream is "recurring" if it appears more than once
        boolean flyingIsRecurring = occurrences.getOrDefault("Flying over mountains", 0) > 1;
        boolean mazeIsRecurring = occurrences.getOrDefault("Lost in maze", 0) > 1;

        Assert.assertTrue(flyingIsRecurring,
                "'Flying over mountains' should appear more than once in the diary");
        Assert.assertTrue(mazeIsRecurring,
                "'Lost in maze' should appear more than once in the diary");

        // Step 4: exactly 2 distinct dream names should be recurring
        long recurringCount = occurrences.values().stream().filter(count -> count > 1).count();
        Assert.assertEquals(recurringCount, 2,
                "Expected exactly 2 distinct dream names to be recurring");

        // Step 5: cross-check against the summary page's stated "Recurring Dreams" number
        DreamsTotalPage totalPage = new DreamsTotalPage(driver);
        totalPage.open(BASE_URL + "dreams-total.html");
        Assert.assertEquals(totalPage.getStat("Recurring Dreams"), (int) recurringCount,
                "Recurring Dreams stat on summary page should match what we calculated from the diary");
    }
}
