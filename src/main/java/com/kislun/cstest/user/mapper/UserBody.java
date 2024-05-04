package com.kislun.cstest.user.mapper;

import com.kislun.cstest.user.model.Address;
import com.kislun.cstest.validation.Adult;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserBody {
    @NotBlank(message = "First name cannot be empty")
    @Length(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Length(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
    private String lastName;

    @Adult(message = "User must be at least {#minAge} years old")
    @Past(message = "Birth date must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Length(min = 2, max = 50, message = "Email should be between 2 and 50 characters")
    private String email;

    private Address address;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Phone number must be in the format +380XXXXXXXXX")
    private String phoneNumber;
}
