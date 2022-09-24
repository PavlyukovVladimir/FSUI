package io.thrive.fs.ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.thrive.fs.help.Constants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class BaseUISelenideTest {
//    private WebDriver driver;

    @BeforeEach
    void setAll(){

        Configuration.driverManagerEnabled = true;
        Configuration.browser = "chrome";
        Configuration.browserVersion = "104";
        Configuration.baseUrl = Constants.BASE_URL;
        Configuration.holdBrowserOpen = true;
        SelenideLogger.addListener(
                "AllureSelenide", new AllureSelenide()
                        .screenshots(true)  // делать скриншоты
                        .savePageSource(false));  // не сохранять копии html страниц
    }

    @AfterEach
    public void tearDown() {
        Selenide.webdriver().driver().getWebDriver().close();
        Selenide.webdriver().driver().getWebDriver().quit();
    }
}
