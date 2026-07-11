package com.gradleproject.tests;

import io.qameta.allure.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.gradleproject.support.Report;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Framework Hardening")
@Feature("Reporting Insights")
@Owner("SDET Trainee")
class AllureReporting {


    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Categories split flaky,test and product")
    void categoriesPutGenericFlakyRules() throws IOException{
        Report.step("Test 1: Validating Allure Categories Configuration");
        String categories = Files.readString(Path.of("src/test/resources/allure/categories.json"));
        Report.step("Category file is stored");
        int flakyIndex = categories.indexOf("\"Flaky tests\"");
        int testDefectIndex = categories.indexOf("\"Test defects (broken)\"");
        int productDefectIndex = categories.indexOf("\"Product defects\"");
        Report.step("Index of Each fail categories is stored");
        assertTrue(flakyIndex >=0);
        Report.pass("Flacky Category index is inside the category is above 0");
        assertTrue(testDefectIndex > flakyIndex);
        Report.pass("Flacky Category is above the Test Defect");
        assertTrue(productDefectIndex > flakyIndex);
        Report.pass("Test Category is above the Product Defect");
        assertTrue(categories.contains("\"flaky\": true"));
        Report.pass("Flaky Tag inside category is true");
        assertTrue(categories.contains("timeout|stale element|connection reset"));
        Report.pass("The Categories contain timeout|stale element|connection reset");
        Report.pass("Allure categories.json validated successfully.");
    }

    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should appear under Product defects")
    void productDefect() {
        Report.step("Test 2: Creating a Fake Product defect by assert 10==20");
        Report.fail("Intentiallly failed for allure report categorisation");
        assertEquals(10, 20, "Incorrect cart total");
    }

    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should appear under Test defects (broken)")
    void brokenDefect() {
        Report.step("Test 3: Creating a Fake Test defect checking null text length");
        String text = null;
        Report.step("Test is initialised with null");
        Report.step("String length() method is used assert");
        Report.fail("Intentiallly failed for allure report categorisation");
        text.length();
    }

    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should appear under Test defects (broken)")
    void brokenDefect1() {
        Report.step("Test 3: Creating a Fake Test defect By throwing any RunTimeException");
        Report.fail("Intentiallly throw error for allure report categorisation");
        throw new RuntimeException("Runtime Exception Error");
    }

    @Test
    @Disabled
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The Test will skip and add to skipped category")
    void skippedTest() {}

}
 