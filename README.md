[![Build status](https://ci.appveyor.com/api/projects/status/nr42s84gnwya1so4/branch/master?svg=true)](https://ci.appveyor.com/project/PavlyukovVladimir/fsui/branch/master)

# How to use:

## On a local machine:

* Runs ALL tests with clean old tests results: `./gradlew clean test`
* Runs ALL tests without clean old tests results: `./gradlew test`
* **Cleans old tests results**: `./gradlew clean`
* **Tests**(*without clean old tests results*):
  * **API**:
    * Runs all API tests: `./gradlew test --tests io.thrive.fs.api.tests.*`
    * [Positive registration scenario with a referral code](io.thrive.fs.api.tests.ApiRegistrationTest.registrationScenarioWithReferCode) `./gradlew test --tests io.thrive.fs.api.tests.ApiRegistrationTest.registrationScenarioWithReferCode`
    * [Positive registration scenario without a referral code](io.thrive.fs.api.tests.ApiRegistrationTest.registrationScenarioWithoutReferCode) `./gradlew test --tests io.thrive.fs.api.tests.ApiRegistrationTest.registrationScenarioWithoutReferCode`
  * **UI**:
    * Runs all UI tests: `./gradlew test --tests io.thrive.fs.ui.tests.HappyFlowRegisteringNewUserTest`
* Generate allure project:
    * `./gradlew allureReport` - generate Allure report
    * [view the report](build/reports/allure-report/allureReport/index.html)
    * `./gradlew allureServe` - generate Allure report and opens it in the default browser

## CI/CD:

Each commit initiates the launch of tests, packs the folder with reports into an archive and transfers it to artifacts

To view the test report, you must:

1. Download and unzip the artifact [reports.zip](https://ci.appveyor.com/api/buildjobs/9cq9h5iauk4ij8fy/artifacts/reports.zip).

2. View a report from an unzipped artifact.

   1. When using it for the first time
      1. In the firefox browser, enter 'about:config' in the address bar
      2. After taking the risk and clicking ***'show all'***, set ***security.fileuri.strict_origin_policy*** to ***false***
   2. After these settings, you can open ***index.html*** using the firefox browser

