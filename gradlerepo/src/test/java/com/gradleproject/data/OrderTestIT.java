package com.gradleproject.data;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


@Testcontainers(disabledWithoutDocker = true)
 class OrderTestIT {
     @Container
     static MySQLContainer mySQL = new MySQLContainer(DockerImageName.parse("mysql:8.0"))
             .withDatabaseName("retail_test")
             .withUsername("root")
             .withPassword("root"); 

     static OrderRepository repository;
     static OrderFactory factory;

     @BeforeAll
     static void migrateSchema(){
         Flyway.configure()
                 .dataSource(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword())
                 .locations("classpath:db/migration")
                 .load()
                 .migrate();

         repository = new OrderRepository(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword());
         factory = new OrderFactory(repository);
     }

     @BeforeEach
     void reset(){
         repository.resetMutableTables();
     }

     @Test
     void flywaySeedingReferenceDataButNoPerTestOrders(){
         assertEquals(4,repository.referenceStatusCount());
         assertEquals(0,repository.count());
     }
     
     @Test
    void persistedBuilderDataAgainstIsolatedMysql(){
         long id = factory.persisted(OrderBuilder.anOrder().qty(3));
         
         assertTrue(id>0);
         assertEquals(1,repository.count());
     }
     
    @Test
    void countsOnlyPersistedTestOrders(){
         factory.persisted(OrderBuilder.anOrder());
         factory.persisted(OrderBuilder.anOrder().sku("SKU-2").qty(2));
         
         assertEquals(2,repository.count());
    }
    
    @Test
    void resetMakesTestOrderIndependent(){
         assertEquals(0,repository.count());
         factory.persisted(OrderBuilder.anOrder().refunded());
         
         assertEquals(1,repository.count());
         assertEquals(1,repository.countByStatus("REFUNDED"));
    }
      
}
 