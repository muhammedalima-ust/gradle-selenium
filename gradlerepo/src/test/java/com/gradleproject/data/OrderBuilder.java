package com.gradleproject.data;

import java.time.LocalDate;

import com.gradleproject.models.Orders;

public class OrderBuilder {

private  String sku = "SKU-1" ;
private  int qty = 1 ;
private  long price = 1299_00 ;
private  String status = "NEW" ;
private  LocalDate createdon = LocalDate.now() ;
private  boolean refunded =false;
private OrderBuilder(){
}

public static OrderBuilder anOrder(){
    return new OrderBuilder();
}


public OrderBuilder sku(String sku){
    this.sku=sku;
    return this;
}

public OrderBuilder qty(int qty){
    this.qty=qty;
    return this;
}
public OrderBuilder price(long price){
    this.price=price;
    return this;
}

public OrderBuilder status(String status){
    this.status=status;
    return this;
}
public OrderBuilder createdon(LocalDate createdon){
    this.createdon=createdon;
    return this;
}
public OrderBuilder refunded(){
    this.refunded=true;
    this.status="REFUNDED";
    return this;
}

public Orders build(){
     if (qty < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1");
        }

        if (price < 1) {
            throw new IllegalArgumentException("totalPaise must be positive");
        }
        
    return new Orders(this.sku,this.qty,this.price,this.status,this.createdon,this.refunded);
}

}
