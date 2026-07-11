package com.gradleproject.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Allure;

public class Report {
    private static final Logger log = LoggerFactory.getLogger(Report.class);

    public static void step(String message) {
        log.info("▶ {}", message);
        Allure.step(message);
    }

    public static void pass(String message) {
        log.info("✔ {}", message);
        Allure.step("PASS : " + message);
    }

    public static void fail(String message) {
        log.error("✖ {}", message);
        Allure.step("FAIL : " + message);
    }

    public static void info(String key, Object value) {
        log.info("   {} : {}", key, value);
    }
}
