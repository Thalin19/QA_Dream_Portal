package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DreamsDiaryPage;

import java.util.List;

public class DreamsDiaryTest extends BaseTest {

    @Test(description = "Diary table should have exactly 10 dream entries")
    public void testExactlyTenDreamEntries() {
        DreamsDiaryPage diaryPage = new DreamsDiaryPage(driver);
        diaryPage.open(BASE_URL + "dreams-diary.html");

        Assert.assertEquals(diaryPage.getDreamCount(), 10,
                "There should be exactly 10 rows in the dream diary table");
    }

    @Test(description = "Every dream's type should be either 'Good' or 'Bad', nothing else")
    public void testDreamTypesAreOnlyGoodOrBad() {
        DreamsDiaryPage diaryPage = new DreamsDiaryPage(driver);
        diaryPage.open(BASE_URL + "dreams-diary.html");

        List<String> types = diaryPage.getAllDreamTypes();
        for (String type : types) {
            Assert.assertTrue(type.equals("Good") || type.equals("Bad"),
                    "Unexpected dream type found: '" + type + "'");
        }
    }

    @Test(description = "Every row should have all 3 columns filled in (non-empty)")
    public void testEveryRowHasAllColumnsFilled() {
        DreamsDiaryPage diaryPage = new DreamsDiaryPage(driver);
        diaryPage.open(BASE_URL + "dreams-diary.html");

        for (DreamsDiaryPage.DreamRow row : diaryPage.getAllDreamRows()) {
            Assert.assertFalse(row.name.isEmpty(), "Dream Name should not be empty");
            Assert.assertFalse(row.daysAgo.isEmpty(), "Days Ago should not be empty");
            Assert.assertFalse(row.type.isEmpty(), "Dream Type should not be empty");
        }
    }
}
