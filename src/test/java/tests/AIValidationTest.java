package tests;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;
import pages.DreamsDiaryPage;

import java.io.IOException;
import java.util.List;

/**
 * BONUS (Part 2, optional): asks OpenAI to independently classify each dream
 * name as "Good" or "Bad" and compares that against what the table says.
 *
 * This is entirely separate from the core tests above - do those first,
 * come back to this once the rest of the assignment works.
 *
 * SETUP REQUIRED:
 * 1. Get an API key from https://platform.openai.com/api-keys (needs a paid account,
 *    a few cents of usage is enough for this).
 * 2. Set it as an environment variable before running tests, e.g. in a terminal:
 *      export OPENAI_API_KEY=sk-...        (Mac/Linux)
 *      setx OPENAI_API_KEY "sk-..."        (Windows)
 * 3. Don't hardcode the key in this file or commit it to GitHub.
 */
public class AIValidationTest extends BaseTest {

    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Test(description = "AI classification of each dream name should agree with the table's Dream Type")
    public void testAIClassificationMatchesTable() throws IOException {
        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            throw new SkipException("OPENAI_API_KEY environment variable not set - skipping AI bonus test");
        }

        DreamsDiaryPage diaryPage = new DreamsDiaryPage(driver);
        diaryPage.open(BASE_URL + "dreams-diary.html");
        List<DreamsDiaryPage.DreamRow> rows = diaryPage.getAllDreamRows();

        int mismatches = 0;
        StringBuilder mismatchDetails = new StringBuilder();

        for (DreamsDiaryPage.DreamRow row : rows) {
            String aiClassification = classifyDream(row.name, apiKey);
            if (!aiClassification.equalsIgnoreCase(row.type)) {
                mismatches++;
                mismatchDetails.append(String.format(
                        "%n  '%s' -> table says '%s', AI says '%s'",
                        row.name, row.type, aiClassification));
            }
        }

        // Note: this is a "soft" assertion in spirit - AI classification is subjective,
        // so document mismatches rather than necessarily treating every one as a bug.
        System.out.println("AI vs table mismatches: " + mismatches + mismatchDetails);
        Assert.assertTrue(mismatches <= 2,
                "Too many disagreements between AI and table classification:" + mismatchDetails);
    }

    /** Sends one dream name to OpenAI and asks for a single-word Good/Bad classification. */
    private String classifyDream(String dreamName, String apiKey) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject message = new JSONObject();
        message.put("role", "user");
        message.put("content", "Classify this dream as either 'Good' or 'Bad' only, " +
                "respond with just that single word, nothing else. Dream: " + dreamName);

        JSONObject body = new JSONObject();
        body.put("model", "gpt-4o-mini");
        body.put("messages", new JSONArray().put(message));
        body.put("temperature", 0);

        RequestBody requestBody = RequestBody.create(
                body.toString(), MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(OPENAI_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            String content = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
            return content.trim();
        }
    }
}
