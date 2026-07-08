package com.gradleproject.bdd;

import org.openqa.selenium.WebDriver;

import com.gradleproject.pages.CartPage;
import com.gradleproject.pages.CatalogPage;
import com.gradleproject.pages.CheckoutPage;
import com.gradleproject.pages.ProductPage;
import com.gradleproject.pages.components.Header;

public class World {
    public WebDriver driver;
    public CatalogPage catalog;
    public CheckoutPage checkout;
    public ProductPage product;
    public CartPage cart;

    public Header header(){
        return new Header(driver);
    }
}
