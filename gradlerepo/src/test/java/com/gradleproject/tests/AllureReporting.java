package com.gradleproject.tests;

import io.qameta.allure.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Epic("Framework Hardening")
@Feature("Reporting Insights")
@Owner("SDET Trainee")
class AllureReporting {
    private static final Logger log =
            LoggerFactory.getLogger(AllureReporting.class);


    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Categories split flaky,test and product")
    void categoriesPutGenericFlakyRules() throws IOException{
        log.info("Test 1: Validating Allure Categories Configuration");
        String categories = Files.readString(Path.of("src/test/resources/allure/categories.json"));

        int flakyIndex = categories.indexOf("\"Flaky tests\"");
        int testDefectIndex = categories.indexOf("\"Test defects (broken)\"");
        int productDefectIndex = categories.indexOf("\"Product defects\"");

        assertTrue(flakyIndex >=0);
        assertTrue(testDefectIndex > flakyIndex);
        assertTrue(productDefectIndex > flakyIndex);
        assertTrue(categories.contains("\"flaky\": true"));
        assertTrue(categories.contains("timeout|stale element|connection reset"));
        log.info("Allure categories.json validated successfully.");
        
    }

    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should appear under Product defects")
    void productDefect() {
        log.info("Test 2: Creating a Fake Product defect by assert 10==20");
        assertEquals(10, 20, "Incorrect cart total");
    }

    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should appear under Test defects (broken)")
    void brokenDefect() {
        log.info("Test 3: Creating a Fake Test defect checking null text length");
        String text = null;
        text.length();
    }

    @Test
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Should appear under Test defects (broken)")
    void brokenDefect1() {
        log.info("Test 3: Creating a Fake Test defect By throwing any RunTimeException");
        throw new RuntimeException("Runtime Exception Error");
    }

    @Test
    @Disabled
    @Story("Categories")
    @Severity(SeverityLevel.CRITICAL)
    @Description("The Test will skip and add to skipped category")
    void skippedTest() {}

}
 