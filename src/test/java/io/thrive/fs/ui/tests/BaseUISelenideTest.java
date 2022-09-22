package io.thrive.fs.ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.thrive.fs.help.Constants;
import org.junit.jupiter.api.BeforeEach;


public class BaseUISelenideTest {

    @BeforeEach
    void setAll(){
        Configuration.browser = "chrome";
        Configuration.baseUrl = Constants.BASE_URL;
        Configuration.holdBrowserOpen = true;
        SelenideLogger.addListener(
                "AllureSelenide", new AllureSelenide()
                        .screenshots(true)  // делать скриншоты
                        .savePageSource(false));  // не сохранять копии html страниц
    }
}
