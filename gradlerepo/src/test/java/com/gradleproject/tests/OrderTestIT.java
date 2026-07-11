package com.gradleproject.tests;

import io.qameta.allure.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.gradleproject.data.OrderBuilder;
import com.gradleproject.data.OrderFactory;
import com.gradleproject.data.OrderRepository;
import com.gradleproject.support.Report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testcontainer Features:
 * MySql Test Containers
 * Flyway for SeedData
 * Builder And Factory for Data Format
 * Log for DB Logging
 * Allure Reporting and Tracing
 * Test isolation
 */
@Epic("Testcontainer")
@Feature("Datamigration And Test Container Test")
@Owner("SDET Trainee")
@Testcontainers(disabledWithoutDocker = true)
class OrderTestIT {

    // Dedicated MySQL for testcontainer.
    @Container
    static MySQLContainer mySQL = new MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("retail_test")
            .withUsername("root")
            .withPassword("root");

    static OrderRepository repository;
    static OrderFactory factory;

    // Run Flyway Migration , Initialising Repository and Factory.
    @BeforeAll
    static void migrateSchema(){

        Report.step("========== Test Container Test Started ==========");

        Report.info("MySql Container Running       : {}", mySQL.isRunning());
        Report.info("MySql Container JDBC URL      : {}", mySQL.getJdbcUrl());
        Report.info("MySql Container Database      : {}", mySQL.getDatabaseName());

        Report.step("Flyway Migration Started");
        Flyway.configure()
                .dataSource(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
        Report.step("Flyway Migration Completed");


        repository = new OrderRepository(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword());
        Report.step("Repository Initialised");
        factory = new OrderFactory(repository);
        Report.step("Factory Initialised");
    }

    @BeforeEach
    void reset(){
        Report.step("Resetting database...");
        repository.resetMutableTables();
        Report.pass("Database reset complete.");
    }

    @Test
        
        void flywaySeedingReferenceDataButNoPerTestOrders(){
            Report.step("Test 1: Flyway Migration Testing Started");
            assertEquals(4,repository.referenceStatusCount());
            Report.pass("Order Status DB is created and 4 row is there");
            
            assertEquals(0,repository.count());
            Report.pass("Orders DB is created and data is empty");
            Report.pass("Test 1:PASSED");
        }
        
    @Test
    void persistedBuilderDataAgainstIsolatedMysql(){
            Report.step("Test 2: Data Persisted Test for Factory");
            long id = factory.persisted(OrderBuilder.anOrder().qty(3));
            Report.step("Order is created and id is stored in variable");
            assertTrue(id>0);
            Report.pass("Created id is positive value");
            assertEquals(1,repository.count());
            Report.pass("Order DB has 1 row");
            Report.pass("Test 2:PASSED");
        }
        
    @Test
    void countsOnlyPersistedTestOrders(){
            Report.step("Test 3: Data Persisted Test for Factory is consistant");
            factory.persisted(OrderBuilder.anOrder());
            Report.step("Order is created");
            factory.persisted(OrderBuilder.anOrder().sku("SKU-2").qty(2));
            Report.step("Another Order is created");
            
            assertEquals(2,repository.count());
            Report.pass("Order DB has 2 row");
            Report.pass("Test 3:PASSED");
    }

    @Test
    void resetMakesTestOrderIndependent(){
            Report.step("Test 4: Order Created is Exactly what we used");
            assertEquals(0,repository.count());
            Report.pass("Order DB has 0 row");
            factory.persisted(OrderBuilder.anOrder().refunded());
            Report.step("Order is created");
            
            assertEquals(1,repository.count());
            Report.pass("Order DB has 1 row");
            assertEquals(1,repository.countByStatus("REFUNDED"));
            Report.pass("Order DB has 1 row ordr with status REFUNDED");
            Report.pass("Test 4:PASSED");
    }
}