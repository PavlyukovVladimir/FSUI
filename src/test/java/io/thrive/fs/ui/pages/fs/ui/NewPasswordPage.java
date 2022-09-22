package io.thrive.fs.ui.pages.fs.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;

public class NewPasswordPage {
    private static String endpoint = "auth/new-password";
    private static String token;

    public NewPasswordPage(String registrationToken){
         token = registrationToken;
    }

    public String getEndpoint(){
        return endpoint + "?token=" + token;
    }
    private SelenideElement fldPassword = $("#password");
    private SelenideElement fldConfirmationPassword = $("#passwordConfirmation");
    private SelenideElement btnSubmit = $("button");

    public void setPassword(String password){
        fldPassword.setValue(password);
    }

    public void setConfirmationPassword(String ConfirmationPassword){
        fldConfirmationPassword.setValue(ConfirmationPassword);
    }

    public void btnSubmitClick(){
        btnSubmit.shouldBe(Condition.visible, Duration.ofSeconds(5)).click();
    }
}
