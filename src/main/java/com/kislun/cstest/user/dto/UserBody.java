package com.kislun.cstest.user.dto;

import com.kislun.cstest.user.model.Address;
import com.kislun.cstest.validation.annotation.Adult;
import com.kislun.cstest.validation.group.OnPatch;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.groups.Default;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UserBody {
    @NotBlank(message = "firstName cannot be empty", groups = Default.class)
    @Length(min = 2, max = 50, message = "First name should be between 2 and 50 characters", groups = {Default.class, OnPatch.class})
    private String firstName;

    @NotBlank(message = "lastName cannot be empty", groups = Default.class)
    @Length(min = 2, max = 50, message = "Last name should be between 2 and 50 characters", groups = {Default.class, OnPatch.class})
    private String lastName;

    @Adult(groups = {Default.class, OnPatch.class})
    @Past(message = "Birth date must be in the past", groups = {Default.class, OnPatch.class})
    @NotNull(message = "Birth date cannot be empty. Format: yyyy-MM-dd", groups = Default.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;

    @NotBlank(message = "Email cannot be empty", groups = Default.class)
    @Email(message = "Email should be valid", groups = {Default.class, OnPatch.class})
    @Length(min = 2, max = 50, message = "Email should be between 2 and 50 characters", groups = {Default.class, OnPatch.class})
    private String email;

    private Address address;

    @Pattern(regexp = "^\\+380\\d{9}$", message = "Phone number must be in the format +380XXXXXXXXX", groups = {Default.class, OnPatch.class})
    private String phoneNumber;
}
