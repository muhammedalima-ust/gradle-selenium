package com.gradleproject.tests;

import io.qameta.allure.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.gradleproject.data.OrderBuilder;
import com.gradleproject.data.OrderFactory;
import com.gradleproject.data.OrderRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Epic("Testcontainer Tests")
@Feature("Datamigration And Test Container Test")
@Owner("SDET Trainee")
@Testcontainers(disabledWithoutDocker = true)
class OrderTestIT {

    private static final Logger log =
            LoggerFactory.getLogger(OrderTestIT.class);

    @Container
    static MySQLContainer mySQL = new MySQLContainer(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("retail_test")
            .withUsername("root")
            .withPassword("root");

    static OrderRepository repository;
    static OrderFactory factory;

    @BeforeAll
    static void migrateSchema(){

        log.info("========== Test Container Test Started ==========");

        log.info("MySql Container Running       : {}", mySQL.isRunning());
        log.info("MySql Container JDBC URL      : {}", mySQL.getJdbcUrl());
        log.info("MySql Container Database      : {}", mySQL.getDatabaseName());

        log.info("========== Flyway Migration Started ==========");
        Flyway.configure()
                .dataSource(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword())
                .locations("classpath:db/migration")
                .load()
                .migrate();
        log.info("========== Flyway Migration Completed ==========");


        repository = new OrderRepository(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword());
        log.info("Repository Initialised");
        factory = new OrderFactory(repository);
        log.info("Factory Initialised");
    }

    @BeforeEach
    void reset(){
        log.info("========== Clearing Table Started ==========");
        repository.resetMutableTables();
        log.info("========== Clearing Table Completed ==========");
    }

    @Test
        
        void flywaySeedingReferenceDataButNoPerTestOrders(){
            log.info("Test 1: Flyway Migration Testing Started");
            assertEquals(4,repository.referenceStatusCount());
            assertEquals(0,repository.count());
            log.info("Test 1: Completed");
            log.info("Tested Order Status DB have 4 Row and Retail Order DB Have 0 Rows : Test Passed");
        }
        
    @Test
    void persistedBuilderDataAgainstIsolatedMysql(){
            log.info("Test 2: Data Persisted Test for Factory");
            long id = factory.persisted(OrderBuilder.anOrder().qty(3));
            
            assertTrue(id>0);
            assertEquals(1,repository.count());
            log.info("Test 2: Completed");
            log.info("Tested Order ID is greater than 0 and Retail Order DB have a row : Test Passed");
        }
        
    @Test
    void countsOnlyPersistedTestOrders(){
            log.info("Test 3: Data Persisted Test for Factory is consistant");
            factory.persisted(OrderBuilder.anOrder());
            factory.persisted(OrderBuilder.anOrder().sku("SKU-2").qty(2));
            
            assertEquals(2,repository.count());
            log.info("Test 3: Completed");
            log.info("Tested the Retail Order DB Have 2 Row : Test Passed");
        
    }

    @Test
    void resetMakesTestOrderIndependent(){
            log.info("Test 4: Order Created is Exactly what we used");
            assertEquals(0,repository.count());
            factory.persisted(OrderBuilder.anOrder().refunded());
            
            assertEquals(1,repository.count());
            assertEquals(1,repository.countByStatus("REFUNDED"));
            log.info("Test 4: Completed");
            log.info("Tested the retail order db is row is 1 and retail order db with filter status REFUNDED is 1 : Test Passed");
    }
}