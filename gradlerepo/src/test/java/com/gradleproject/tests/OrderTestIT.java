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

        Report.step("========== Flyway Migration Started ==========");
        Flyway.configure()
                .dataSource(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
        Report.step("========== Flyway Migration Completed ==========");


        repository = new OrderRepository(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword());
        Report.step("Repository Initialised");
        factory = new OrderFactory(repository);
        Report.step("Factory Initialised");
    }

    @BeforeEach
    void reset(){
        Report.step("Resetting database...");
        repository.resetMutableTables();
        Report.step("Database reset complete.");
    }

    @Test
        
        void flywaySeedingReferenceDataButNoPerTestOrders(){
            Report.step("Test 1: Flyway Migration Testing Started");
            assertEquals(4,repository.referenceStatusCount());

            assertEquals(0,repository.count());
            Report.step("Test 1: Completed");
            Report.step("Tested Order Status DB have 4 Row and Retail Order DB Have 0 Rows : Test Passed");
        }
        
    @Test
    void persistedBuilderDataAgainstIsolatedMysql(){
            Report.step("Test 2: Data Persisted Test for Factory");
            long id = factory.persisted(OrderBuilder.anOrder().qty(3));
            
            assertTrue(id>0);
            assertEquals(1,repository.count());
            Report.step("Test 2: Completed");
            Report.step("Tested Order ID is greater than 0 and Retail Order DB have a row : Test Passed");
        }
        
    @Test
    void countsOnlyPersistedTestOrders(){
            Report.step("Test 3: Data Persisted Test for Factory is consistant");
            factory.persisted(OrderBuilder.anOrder());
            factory.persisted(OrderBuilder.anOrder().sku("SKU-2").qty(2));
            
            assertEquals(2,repository.count());
            Report.step("Test 3: Completed");
            Report.step("Tested the Retail Order DB Have 2 Row : Test Passed");
        
    }

    @Test
    void resetMakesTestOrderIndependent(){
            Report.step("Test 4: Order Created is Exactly what we used");
            assertEquals(0,repository.count());
            factory.persisted(OrderBuilder.anOrder().refunded());
            
            assertEquals(1,repository.count());
            assertEquals(1,repository.countByStatus("REFUNDED"));
            Report.step("Test 4: Completed");
            Report.step("Tested the retail order db is row is 1 and retail order db with filter status REFUNDED is 1 : Test Passed");
    }
}