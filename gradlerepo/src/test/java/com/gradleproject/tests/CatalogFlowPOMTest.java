package com.gradleproject.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;

import com.gradleproject.pages.CartPage;
import com.gradleproject.pages.CatalogPage;
import com.gradleproject.pages.ProductPage;
import com.gradleproject.support.DriverFactory;
public class CatalogFlowPOMTest {

    private static WebDriver driver;

    @BeforeEach
    void setup(){
        driver = DriverFactory.createChromeDriver();
    }

    @AfterEach
    void cleanup(){
        if(driver!=null){
            driver.quit();
        }
    }

    @Test
    void searchFindson(){
        CatalogPage catalog = new CatalogPage(driver).open().searchFor("headphones","Showing 1 product");

    List<String> titles = catalog.titles();

    assertAll(
        ()-> assertFalse(titles.isEmpty(),"Search product not found"),
        ()-> assertTrue(titles.stream().allMatch((title)->title.toLowerCase().contains("headphones")),"search result should be related to headphones")
    );
    }

    @Test
    @DisplayName("POM sort query based on the price")
    void sortProductsBasedOnthePrice(){
        CatalogPage catalogPage = new CatalogPage(driver)
                .open()
                .sortby("Price: Low to High");

        List<Integer> prices = catalogPage.prices();
        assertEquals(prices.stream().sorted().toList(),prices);

    }

    @Test
    @DisplayName("POM full journey")
    void full_journey_of_all_stages(){
        CatalogPage catalogPage = new CatalogPage(driver)
                .open()
                .searchFor("headphones","Showing 1 product");

        ProductPage productPage = catalogPage.openFirstProduct();
        
        assertTrue(productPage.name().toLowerCase().contains("headphones"));

        CartPage cartPage = productPage.addToCart();
        cartPage.header().cartBadge().expectCount(1);

        assertAll(
                ()->assertEquals(1,cartPage.lineCount()),
                ()->assertFalse(cartPage.total().isBlank())
        );

        String Confirmation = cartPage.proceed()
                .placeOrder().confirmationText();

        assertTrue(Confirmation.toLowerCase().contains("confirmed"));


    }
}