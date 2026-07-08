// package com.gradleproject.tests;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import org.junit.jupiter.api.Test;

// import com.gradleproject.Data.OrderFactory;
// import com.gradleproject.data.OrderRepository;

// import static com.gradleproject.Data.OrderFactory.Repo;
// import static com.gradleproject.Data.OrderBuilder.anOrder;
// public class Day6Test {
//     private final OrderRepository repository;
//     private final OrderFactory factory;

//     @BeforeAll
//     void setup(){
//         repository = new OrderRepository();
//         factory = new OrderFactory(repository);
//     }

//     @Test
//     void createOrder(){
//         System.out.println("Before add"+Repo.size());
//         factory.persisted(anOrder());
//         System.out.println("after add"+Repo.size());
//     }

//     @Test
//     void countOrders(){
//         assertEquals(repository.size(),1);
//     }
    
// }