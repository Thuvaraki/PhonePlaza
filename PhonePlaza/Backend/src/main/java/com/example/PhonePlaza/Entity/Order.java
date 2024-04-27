package com.example.PhonePlaza.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "customer_order") //Order is reserved keyword in sql..So have to give a different table name
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private long userId;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String addressLine1;

    @NotNull
    private String addressLine2;

    @NotNull
    private String city;

    @NotNull
    private String district;

    @NotNull
    private String phoneNo;

    private LocalDateTime placedOn;


    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private Set<OrderItem> orderItem =new HashSet<>();

    public Order(long userId, String firstName, String lastName, String addressLine1, String addressLine2, String city, String district, String phoneNo, LocalDateTime placedOn, Set<OrderItem> orderItem) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.district = district;
        this.phoneNo = phoneNo;
        this.placedOn = placedOn;
        this.orderItem = orderItem;
    }

    //    Assume this application has only cash on delivery
}
