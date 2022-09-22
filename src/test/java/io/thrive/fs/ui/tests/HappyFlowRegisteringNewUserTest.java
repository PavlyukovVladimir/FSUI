package io.thrive.fs.ui.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.LocalStorage;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.*;
import io.thrive.fs.api.common.AuthMethods;
import io.thrive.fs.api.common.UsersMethods;
import io.thrive.fs.help.PostgrestDataBase;
import io.thrive.fs.help.DataGenerator;
import io.thrive.fs.ui.pages.fs.ui.*;
import io.thrive.fs.ui.pages.stripe.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.*;
import org.junit.platform.commons.util.ClassUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.codeborne.selenide.Selenide.switchTo;


@Epic("Full registering new user")
@Feature("Registration user")
@Severity(SeverityLevel.CRITICAL)
public class HappyFlowRegisteringNewUserTest extends BaseUISelenideTest{

    @Test()
    @DisplayName("Регистрируем нового пользователя вместе со Stripe аккаунтом")
    @Story("Happy flow registration new user with Stripe")
    @Description("From UI it registers new user and registers Stripe account hor him")
    public void registrationNewUserHappyTest(){

        DataGenerator dataGenerator = new DataGenerator();
        // открываем страницу регистрации нового пользователя
        String referSuffix = "?referCode=eyJ1c2VySWQiOjM5Nn0=";
        Selenide.open(RegistrationPage.endpoint + referSuffix);
        WebDriverRunner.getWebDriver().manage().window().maximize();
        // создаем "ждалку"
        WebDriverWait wait = new WebDriverWait(WebDriverRunner.getWebDriver(), Duration.ofSeconds(10));
        // объект с методами страницы регистрации
        RegistrationPage registrationPage = new RegistrationPage();
        // Ждем когда текущий url сменится на нужный
        wait.until(ExpectedConditions.urlToBe(Configuration.baseUrl + RegistrationPage.endpoint + referSuffix));
        // Запишем случайное Бразильское имя
        String fullName = dataGenerator.generateFullName("pt-BR");
        registrationPage.setFullName(fullName);
        // Создаем имейл
        String email = dataGenerator.getEmail();
        registrationPage.setEmail(email);
        // Создаем телефон
        String phone = dataGenerator.getPhone();
//        registrationPage.setPhone("1"+phone);
        registrationPage.setPhone(phone);
        // "Случайно" выберем из предложенных на форме сайта стран (только Бразилия)
        registrationPage.setCountryRandomly();
        // Случайно выберем штат из предложенных на форме сайта
        registrationPage.setStateRandomly();
        // Создаем Название города
        String city = dataGenerator.generateCity("pt-BR");
        registrationPage.setCity(city);
        // включим панель разработчика
        String buttonName = "F12";
        Keys key = Keys.valueOf(buttonName.toUpperCase());
        switchTo().activeElement().sendKeys(key);
        // Отправим форму на регистрацию(клик по кнопке)
        registrationPage.registrationClick();
        // Ждем когда текущий url поменяется
        wait.until(ExpectedConditions.urlToBe(Configuration.baseUrl+ LoginPage.endpoint));

        // через api логинимся в админку и подтверждаем регистрацию пользователя
        // TODO надо бы это через UI переделать
        AuthMethods authMethods = new AuthMethods(Configuration.baseUrl + "api");
        JSONObject adminResponse = authMethods.adminLogin("root@admin.com","rootadmin");
        String adminToken = (String) adminResponse.get("accessToken");
        UsersMethods usersMethods = new UsersMethods(Configuration.baseUrl + "api");
        List<JSONObject> pendingRegResponse = usersMethods.usersPending(adminToken);
        long userId = 0L;
        for(var usr : pendingRegResponse){
            String usrEmail = (String) usr.get("email");
            Integer usrId = (Integer) usr.get("id");
            if(usrEmail.equals(email)){
                Assertions.assertEquals(0L, userId, "Duplicate registration requests with userId:" + usrId);
                userId = usrId.longValue();
            }
        }
        Assertions.assertNotEquals(0L, userId, "No registration requests found from email:" + email);
        // подтвердим регистрацию пользователя
        usersMethods.usersApprove(adminToken, userId);

        // Далее из базы получаем токен подтверждения регистрации(чтобы не лезть в мыло)
        // TODO надо бы получение токена из мыла тоже автоматизировать
        PostgrestDataBase postgrest = new PostgrestDataBase();

        String registrationToken = postgrest.getToken(userId, "registration");
        // получаем пароль
        String pass = dataGenerator.getPassword();
        // установим пароль
        // Объект страницы установки пароля
        NewPasswordPage newPasswordPage = new NewPasswordPage(registrationToken);
        // перейдем на страницу установки пароля
        Selenide.open(Configuration.baseUrl + newPasswordPage.getEndpoint());

        newPasswordPage.setPassword(pass);
        newPasswordPage.setConfirmationPassword(pass);
        newPasswordPage.btnSubmitClick();

//        usersMethods.usersSetPassword(registrationToken, pass);
        // Объект с методами страницы авторизации
        LoginPage loginPage = new LoginPage();
        wait.until(ExpectedConditions.urlToBe(Configuration.baseUrl + LoginPage.endpoint));
        // авторизуемся
        loginPage.setUsername(email);
        loginPage.setPassword(pass);
        loginPage.btnLoginClick();
        // главная страница
        wait.until(ExpectedConditions.urlToBe(Configuration.baseUrl));
        // получаем токен
        LocalStorage localStorage = Selenide.localStorage();
        JSONParser parser = new JSONParser();
        JSONObject lsObj;
        try {
            lsObj = (JSONObject)parser.parse(localStorage.getItem("persist:fluency-strikers-dashboard-auth"));
            lsObj = (JSONObject)parser.parse((String)lsObj.get("auth"));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String accessToken = (String)lsObj.get("accessToken");
        // по совпадению user id косвенно проверяем что токен от нужного пользователя
        Assertions.assertEquals(userId, ((Long) lsObj.get("userId")).longValue());
        // Объект с методами главной страницы
        HomePage homePage = new HomePage();
//        // переходим на страницу комиссий
//        homePage.hrfCommissionsClick();
//        // Объект с методами страницы комиссий
//        CommissionsPage commissionsPage = new CommissionsPage();
//        wait.until(ExpectedConditions.urlToBe(Configuration.baseUrl + CommissionsPage.endpoint));
//        // переключаемся на вкладку снятий заработанного
//        commissionsPage.btnCreateClick();
//        wait.withTimeout(Duration.ofSeconds(10))
//                .until(ExpectedConditions.elementToBeClickable(
//                        By.cssSelector("button[data-test=phone-help-text-test-mode]")));
//        // проходим проверку телефона
//        StripeStartPhoneConfirmPage stripeStartPhoneConfirmPage =
//                new StripeStartPhoneConfirmPage(WebDriverRunner.getWebDriver().getCurrentUrl());
//        stripeStartPhoneConfirmPage.setTestZeroPhone();
//        stripeStartPhoneConfirmPage.clickContinue();
//        // вводим тестовый проверочный код
//        StripeSetVerificationCodePage stripeSetVerificationCodePage = new StripeSetVerificationCodePage(
//                stripeStartPhoneConfirmPage.getBaseUrl());
//        stripeSetVerificationCodePage.tapUseTestModeButton();
//        // заполняем регистрационные данные
//        StripeVerifyPersonalDetailsPage stripeVerifyPersonalDetailsPage = new StripeVerifyPersonalDetailsPage(
//                stripeSetVerificationCodePage.getBaseUrl());
//        stripeVerifyPersonalDetailsPage.fillVerijyPersonalDetails(email,phone);
//        // указываем профессию
//        StripeProfessionalDetailsPage stripeProfessionalDetailsPage = new StripeProfessionalDetailsPage(
//                stripeVerifyPersonalDetailsPage.getBaseUrl());
//        stripeProfessionalDetailsPage.checkProfession();
//        // выбираем страйп своим банком для вывода денег
//        StripeSelectPayoutsAccountPage stripeSelectPayoutsAccountPage = new StripeSelectPayoutsAccountPage(
//                stripeProfessionalDetailsPage.getBaseUrl());
//        stripeSelectPayoutsAccountPage.clickUseTestAccount();
//        // итоговые подтверждения введенных данных
//        StripeReviewDetailsPage stripeReviewDetailsPage = new StripeReviewDetailsPage(
//                stripeSelectPayoutsAccountPage.getBaseUrl());
//        stripeReviewDetailsPage.clickUseTestAccount();
//        // проверка что вернулись обратно на сайт https://fluency-strikers.dev.thrive.io
        wait.until(ExpectedConditions.urlToBe(Configuration.baseUrl));
    }
}
