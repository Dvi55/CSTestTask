package com.kislun.cstest.user.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "country", nullable = false, length = 55)
    private String country;

    @Column(name = "city", nullable = false, length = 60)
    private String city;

    @Column(name = "region", nullable = false, length = 60)
    private String region;

    @Column(name = "street", nullable = false, length = 30)
    private String street;

    @Column(name = "building", nullable = false, length = 10)
    private String building;
}