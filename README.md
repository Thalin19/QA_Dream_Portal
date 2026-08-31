# Dream Portal - Automated UI Tests

Selenium (Java) + TestNG automation for https://arjitnigam.github.io/myDreams/

This covers Part 1 (core UI tests) in full, plus an optional Part 2 AI bonus test.

## What's in here

```
dream-portal-tests/
├── pom.xml                  <- Maven config: lists all dependencies
├── testng.xml                <- tells TestNG which test classes to run
├── README.md                 <- this file
└── src/test/java/
    ├── pages/                 <- Page Object Model classes (one per web page)
    │   ├── BasePage.java
    │   ├── HomePage.java          (index.html)
    │   ├── DreamsDiaryPage.java   (dreams-diary.html)
    │   └── DreamsTotalPage.java   (dreams-total.html)
    └── tests/                 <- the actual test cases
        ├── BaseTest.java          (opens/closes browser; auto-screenshots on failure)
        ├── HomePageTest.java
        ├── DreamsDiaryTest.java
        ├── DreamsTotalTest.java
        └── AIValidationTest.java  (optional bonus, needs an OpenAI key)
```

Running the tests also generates a `screenshots/` folder (only for failed
tests - see below) and a `target/` folder (Maven's build output, ignored
by Git).

**Why split into "pages" and "tests"?** This is the Page Object Model (POM)
pattern the assignment asks for. If the website's HTML ever changes, you
only update the page class - not every single test. Tests describe *what*
to check; page classes describe *how* to find things on the page.

## Prerequisites

You need these installed on your computer:

1. **Java JDK 17+** - check with `java -version` in a terminal
2. **Maven** - check with `mvn -version`
3. **Google Chrome** - just needs to be installed, that's it

You do **not** need to manually download `chromedriver` - the
`webdrivermanager` dependency in `pom.xml` does that automatically the
first time you run the tests.

## Running the tests

From inside the `dream-portal-tests` folder, run:

```bash
mvn test
```

This will:
1. Download all dependencies (first run only, needs internet)
2. Open a real Chrome window for each test
3. Run all test methods in `testng.xml`
4. Print pass/fail results to the terminal

You'll see Chrome open and close a bunch of times - that's normal, it's a
fresh browser per test method (keeps tests independent of each other).

### Running just one test class

```bash
mvn test -Dtest=HomePageTest
```

## Viewing the report

Since tests run through Maven's Surefire plugin, the HTML report lands at:

```
target/surefire-reports/index.html
```

Open it in your browser after `mvn test` finishes for a pass/fail summary -
this covers the "Reporting" part of the rubric. You can open it straight
from the terminal on Windows with:

```bash
start target\surefire-reports\index.html
```

If you want something fancier (Allure), that's a nice add-on but not
required to get this working first.

### (Optional) Upgrading to Allure reports

1. Add the `allure-testng` dependency and `allure-maven` plugin to `pom.xml`
   (search "Allure TestNG Maven setup" for the exact version snippet)
2. Run `mvn test`
3. Run `allure serve` (needs the Allure CLI installed) to view a much
   nicer interactive report

## About the AI bonus test (Part 2, optional)

`AIValidationTest.java` sends each dream name to OpenAI and checks if its
"Good"/"Bad" classification matches the table. It's written to **skip
itself automatically** if you haven't set an API key, so it won't break
your build if you decide to skip the bonus.

To enable it:
```bash
export OPENAI_API_KEY=sk-your-key-here     # Mac/Linux
setx OPENAI_API_KEY "sk-your-key-here"      # Windows (restart terminal after)
```

Never hardcode your API key in the code or commit it to Git.

## Screenshots on failure

`BaseTest.java` automatically captures a screenshot whenever a test fails,
saved into a `screenshots/` folder with the test name and a timestamp,
e.g. `screenshots/testExactlyTenDreamEntries_2026-08-31_10-15-02.png`.
Nothing to configure - it just happens as part of `@AfterMethod`. This
satisfies the "include test screenshots" bonus item without you needing
to take any manually, though you're welcome to add a couple of your own
(e.g. the passing terminal output, the HTML report) too.

## What each test actually checks

| Test class | What it verifies |
|---|---|
| `HomePageTest` | Loading spinner appears then disappears (~3s); main content + button become visible; clicking the button opens both diary and total pages in new tabs |
| `DreamsDiaryTest` | Exactly 10 rows exist; every "Dream Type" is only "Good" or "Bad"; no row has an empty column |
| `DreamsTotalTest` | Good=6, Bad=4, Total=10, Recurring=2; cross-checks the recurring count against the actual diary data (rather than just trusting a hardcoded "2") |
| `AIValidationTest` | (bonus) AI's opinion of each dream name vs. the table's stated type |

## Project hygiene

A `.gitignore` excludes `target/`'s compiled build output (classes, etc.)
and IDE files (`.idea/`, `*.iml`) from version control, since those
shouldn't live in source control. `target/surefire-reports/` is
specifically **kept tracked** so the test report is visible in the repo,
and `screenshots/` (auto-captured on test failures) is tracked too, so
graders can see proof of test runs directly without needing to run
anything themselves.

## Troubleshooting

- **"chromedriver version mismatch"** - update Chrome, then re-run; WebDriverManager auto-fixes this on the next run.
- **Tests fail immediately with a NoSuchElementException** - the site may have changed; re-inspect the page with Chrome DevTools (right-click -> Inspect) and update the `By.id(...)` locators in the relevant page class.
- **`mvn` command not found** - Maven isn't installed or isn't on your PATH.
