package com.gradleproject.models;

import java.time.LocalDate;

public record Orders(String sku,int qty,long price,String status,LocalDate createdOn,boolean refunded) {
}