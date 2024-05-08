package com.kislun.cstest.user.model;

import com.kislun.cstest.validation.group.OnPatch;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.groups.Default;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

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

    @Length(min = 2, max = 55, message = "Country should be between 2 and 55 characters", groups = {Default.class, OnPatch.class})
    @Column(name = "country", nullable = false, length = 55)
    private String country;

    @Length(min = 2, max = 60, message = "City should be between 2 and 60 characters", groups = {Default.class, OnPatch.class})
    @Column(name = "city", nullable = false, length = 60)
    private String city;

    @Length(min = 2, max = 60, message = "Region should be between 2 and 60 characters", groups = {Default.class, OnPatch.class})
    @Column(name = "region", nullable = false, length = 60)
    private String region;

    @Length(min = 2, max = 30, message = "Street should be between 2 and 30 characters", groups = {Default.class, OnPatch.class})
    @Column(name = "street", nullable = false, length = 30)
    private String street;

    @Length(min = 2, max = 10, message = "Building should be between 2 and 10 characters", groups = {Default.class, OnPatch.class})
    @Column(name = "building", nullable = false, length = 10)
    private String building;
}