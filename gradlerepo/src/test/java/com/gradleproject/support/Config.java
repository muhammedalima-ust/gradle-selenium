package com.gradleproject.support;
 
public class Config {
    private Config() {}
    public static String baseUrl() {
        return TestEnvironment.required("baseUrl").replaceAll("/$", "");
    }
 
    public static String catalogUrl() {
        return baseUrl() + "/catalog";
    }

    public static String loginUrl() {
        return baseUrl() + "/login";
    }
 
    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("HEADLESS", "false"));
    }
}
 