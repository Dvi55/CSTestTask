package com.kislun.cstest.user.dao;

import com.kislun.cstest.user.model.LocalUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface LocalUserDAO extends JpaRepository<LocalUser, UUID> {
    Page<LocalUser> findByBirthDateBetween(LocalDate birthDateStart, LocalDate birthDateEnd, Pageable pageable);
}
