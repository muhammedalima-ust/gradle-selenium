package com.gradleproject.tests;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gradleproject.support.Config;
import com.gradleproject.support.DriverFactory;

public class Assisesment {

    private static By EMAIL_INPUT = By.cssSelector("[id='email']");
    private static By PASSWORD = By.cssSelector("[id='password']");
    private static By TITLE = By.cssSelector("[id='page-title']");
    private static By BUTTON = By.cssSelector(".button.primary.form-submit");
    // private static By INLINE_ALERT = By.cssSelector(".inline-status");

    private static WebDriver driver;
    private static WebDriverWait wait;
    
    @BeforeAll
    static void setup(){
        driver = DriverFactory.createChromeDriver();
        wait = new WebDriverWait(driver,Duration.ofSeconds(30));
        driver.get(Config.loginUrl());
    }

    @AfterEach
    void cleanup()
    {
           if (driver!=null) {
            driver.quit();
           } 
    }

    @Test
    @DisplayName("Testing Valid Login")
    void ValidLogin(){
        // WebElement inlineAlert = wait.until(ExpectedConditions.visibilityOfElementLocated(INLINE_ALERT));
    
        // wait.until(ExpectedConditions.stalenessOf(inlineAlert));

        WebElement userName = wait.until(ExpectedConditions.visibilityOfElementLocated(EMAIL_INPUT));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(PASSWORD));

        userName.clear();
        userName.sendKeys("customer@example.com");

        password.clear();
        password.sendKeys("Password@123");

        //password.sendKeys(Keys.ENTER);

        WebElement button = wait.until(ExpectedConditions.elementToBeClickable(BUTTON));

        button.click();


        wait.until(ExpectedConditions.textToBePresentInElementLocated(
        TITLE,
        "Welcome"
        ));

        
    }

}
