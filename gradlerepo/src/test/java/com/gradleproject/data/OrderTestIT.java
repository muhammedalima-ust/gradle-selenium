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

import java.sql.DriverManager;
import java.sql.ResultSet;


@Testcontainers(disabledWithoutDocker = true)
 class OrderTestIT {
     @Container
     static MySQLContainer mySQL = new MySQLContainer(DockerImageName.parse("mysql:8.0"))
             .withDatabaseName("retail_test")
             .withUsername("root")
             .withPassword("root"); 

     static OrderRepository repository;
     static OrderFactory factory;

    //  @BeforeAll
    //  static void migrateSchema(){
    //      Flyway.configure()
    //              .dataSource(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword())
    //              .locations("classpath:db/migration")
    //              .load()
    //              .migrate();

    //      repository = new OrderRepository(mySQL.getJdbcUrl(), mySQL.getUsername(), mySQL.getPassword());
    //      factory = new OrderFactory(repository);
    //  }

    @BeforeAll
    static void migrateSchema() throws Exception {

        System.out.println("===== TESTCONTAINERS DEBUG =====");
        System.out.println("JDBC URL  : " + mySQL.getJdbcUrl());
        System.out.println("Username  : " + mySQL.getUsername());
        System.out.println("Password  : " + mySQL.getPassword());
        System.out.println("Container : " + mySQL.getContainerName());
        System.out.println("================================");

        Flyway flyway = Flyway.configure()
                .dataSource(
                        mySQL.getJdbcUrl(),
                        mySQL.getUsername(),
                        mySQL.getPassword())
                .locations("classpath:db/migration")
                .load();

        var result = flyway.migrate();

        System.out.println("Flyway migrations executed : "
                + result.migrationsExecuted);

        try (Connection con = DriverManager.getConnection(
                mySQL.getJdbcUrl(),
                mySQL.getUsername(),
                mySQL.getPassword());
            Statement stmt = con.createStatement()) {

            System.out.println("\n===== TABLES =====");

            ResultSet rs = stmt.executeQuery("SHOW TABLES");

            while (rs.next()) {
                System.out.println(rs.getString(1));
            }

            System.out.println("==================");

        }

        repository = new OrderRepository(
                mySQL.getJdbcUrl(),
                mySQL.getUsername(),
                mySQL.getPassword());

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
 