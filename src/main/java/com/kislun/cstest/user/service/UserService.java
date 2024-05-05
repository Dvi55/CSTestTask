package com.kislun.cstest.user.service;

import com.kislun.cstest.user.dto.UserBody;
import com.kislun.cstest.user.dao.LocalUserDAO;
import com.kislun.cstest.user.mapper.UserMapper;
import com.kislun.cstest.user.model.LocalUser;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final LocalUserDAO localUserDAO;
    private final UserMapper userMapper;

    public UserService(LocalUserDAO localUserDAO, UserMapper userMapper) {
        this.localUserDAO = localUserDAO;
        this.userMapper = userMapper;
    }

    public Optional<LocalUser> getUserById(UUID id) {
        return localUserDAO.findById(id);
    }

    public List<LocalUser> getAllUsers() {
        return localUserDAO.findAll();
    }

    @Transactional
    public LocalUser createUser(UserBody userBody) {
        return localUserDAO.save(userMapper.mapToLocalUser(userBody));
    }

    @Transactional
    public void deleteUserById(UUID id) {
        localUserDAO.deleteById(id);
    }

    @Transactional
    public LocalUser updateUser(LocalUser user, UserBody userBody) {
        if (isEmailExists(userBody.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        userMapper.updateUserFromBody(userBody, user);
        return localUserDAO.save(user);
    }


    public List<LocalUser> searchBetweenDate(LocalDate from, LocalDate to) {
        return localUserDAO.findByBirthDateBetween(from, to);
    }

    public LocalUser patchUser(LocalUser user) {
        return localUserDAO.save(user);
    }

    private boolean isEmailExists(String email) {
        return localUserDAO.findByEmailIgnoreCase(email).isPresent();
    }

}
