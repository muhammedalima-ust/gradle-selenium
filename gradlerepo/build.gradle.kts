plugins {
    java
}

group = "com.gradleproject"
version = "0.1.0"

val seleniumVersion = "4.45.0"
val selenideVersion = "7.16.2"
val junitVersion = "5.14.4"
val cucumberVersion = "7.34.3"
val allureVersion = "2.33.0"
val extentVersion = "5.1.2"
val extentCucumberAdapterVersion = "1.14.0"
val slf4jVersion = "2.0.17"
val testcontainers = "2.0.5"
val flywayVersion = "10.22.0"
val mysqlLibVersion = "8.0.33"

java {
    sourceCompatibility = JavaVersion.VERSION_22
    targetCompatibility = JavaVersion.VERSION_22
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation(platform("io.cucumber:cucumber-bom:$cucumberVersion"))
    testImplementation(platform("io.qameta.allure:allure-bom:$allureVersion"))
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testcontainers"))
    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumVersion")
    testImplementation("com.codeborne:selenide:$selenideVersion")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.cucumber:cucumber-java")
    testImplementation("io.cucumber:cucumber-junit-platform-engine")
    testImplementation("io.cucumber:cucumber-picocontainer")
    testImplementation("org.junit.platform:junit-platform-suite")
    testImplementation("io.qameta.allure:allure-cucumber7-jvm")
    testImplementation("com.aventstack:extentreports:$extentVersion")
    testImplementation("tech.grasshopper:extentreports-cucumber7-adapter:$extentCucumberAdapterVersion")
    testImplementation("org.slf4j:slf4j-simple:$slf4jVersion")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testcontainers")
    testImplementation("org.testcontainers:testcontainers-mysql:$testcontainers")
    testImplementation("org.flywaydb:flyway-core:$flywayVersion")
    testImplementation("org.flywaydb:flyway-mysql:$flywayVersion")
    testImplementation("mysql:mysql-connector-java:$mysqlLibVersion")


}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(17)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperty("baseUrl", providers.gradleProperty("baseUrl").orElse("http://localhost:5173").get())
    systemProperty("headless", providers.gradleProperty("headless").orElse("true").get())
    systemProperty("browser", providers.gradleProperty("browser").orElse("chrome").get())
    systemProperty("build.label", providers.gradleProperty("buildLabel").orElse("gradle-local").get())
    systemProperty("cucumber.publish.quiet", "true")
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.SHORT
    }
}

fun Test.useProjectTestClasses() {
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
}

tasks.register("runAllTests") {
    group = "verification"
    description = "Runs all Selenium, POM, Structure and Cucumber tests"

    dependsOn(
        tasks.test,
        StructureTest,
        CatalogFlowTest,
        CatalogFlowTestPOM,
        cucumberSmoke,
        Order,
        OrderTestIT
    )
}

tasks.test {
    description = "Run the tests"
    include("**/OrderTestIT.class")
    maxParallelForks = 1
}

val StructureTest by tasks.registering(Test::class) {
    description = "Structure Test of catalog page"
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/CatalogStructureTest.class")
}

val OrderTestIT by tasks.registering(Test::class) {
    description = "Structure Test of catalog page"
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/OrderTestIT.class")
}

val Order by tasks.registering(Test::class) {
    description = "Checking whether the data is persistant"
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/Day6Test.class")
}

val CatalogFlowTest by tasks.registering(Test::class) {
    description = "Check the catalog flow test"
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/CatalogFlowTest.class")
    maxParallelForks = 1
}


val CatalogFlowTestPOM by tasks.registering(Test::class) {
    description = "Check the catalog flow test"
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/CatalogFlowPOMTest.class")
    maxParallelForks = 1
}

val cucumberSmoke by tasks.registering(Test::class) {
    description = "Runs Cucumber smoke scenarios through the Gradle JUnit Platform."
    group = "verification"
    useProjectTestClasses()
    useJUnitPlatform()
    include("**/RunCucumberTest.class")
    systemProperty("cucumber.filter.tags", "@smoke")
    maxParallelForks = 1
}


tasks.register("w6d2BuildSummary") {
    description = "Prints the Week 6 Day 2 Maven to Gradle command map."
    group = "help"
    doLast {
        println(
            """
            W6D2 Build Tooling Summary
            Maven compile: mvn clean test-compile
            Gradle compile: ./gradlew clean testClasses
            Maven structure: mvn clean -Dtest=W6D1RefactoringStructureTest test
            Gradle structure: ./gradlew clean w6d1StructureTest
            Gradle smoke: ./gradlew cucumberSmoke -Pheadless=true
            Gradle scan: ./gradlew w6d1StructureTest --scan
            """.trimIndent()
        )
    }
}
