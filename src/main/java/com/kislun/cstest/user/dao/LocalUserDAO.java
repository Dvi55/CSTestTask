package com.kislun.cstest.user.dao;

import com.kislun.cstest.user.model.LocalUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LocalUserDAO extends JpaRepository<LocalUser, UUID> {


    List<LocalUser> findByBirthDateBetween(LocalDate birthDateStart, LocalDate birthDateEnd);

    Optional<LocalUser> findByEmailIgnoreCase(String email);
}
