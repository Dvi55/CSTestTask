package com.kislun.cstest.user.service;

import com.kislun.cstest.user.InvalidRequestException;
import com.kislun.cstest.user.dao.LocalUserDAO;
import com.kislun.cstest.user.dto.UserBody;
import com.kislun.cstest.user.mapper.UserMapper;
import com.kislun.cstest.user.model.Address;
import com.kislun.cstest.user.model.LocalUser;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.*;

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

    public Page<LocalUser> getAllUsers(Pageable pageable) {
        return localUserDAO.findAll(pageable);
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
        userMapper.updateUserFromBody(userBody, user);
        return localUserDAO.save(user);
    }


    public Page<LocalUser> searchBetweenDate(LocalDate from, LocalDate to, Pageable pageable) {
        return localUserDAO.findByBirthDateBetween(from, to, pageable);
    }

    public LocalUser patchUser(LocalUser user, UserBody userBody) {
        if (areAllFieldsNull(userBody)) {
            return null;
        }
        user.setFirstName(userBody.getFirstName() != null ? userBody.getFirstName() : user.getFirstName());
        user.setLastName(userBody.getLastName() != null ? userBody.getLastName() : user.getLastName());
        user.setBirthDate(userBody.getBirthDate() != null ? userBody.getBirthDate() : user.getBirthDate());
        user.setPhoneNumber(userBody.getPhoneNumber() != null ? userBody.getPhoneNumber() : user.getPhoneNumber());
        user.setEmail(userBody.getEmail() != null ? userBody.getEmail() : user.getEmail());

        if (userBody.getAddress() != null) {
            if (user.getAddress() == null) {
                user.setAddress(new Address());
            }
            patchAddress(userBody, user.getAddress());
        }
        return localUserDAO.save(user);
    }


    private void patchAddress(UserBody userBody, Address existAddress) {
        existAddress.setCity(userBody.getAddress().getCity() != null ? userBody.getAddress().getCity() : existAddress.getCity());
        existAddress.setStreet(userBody.getAddress().getStreet() != null ? userBody.getAddress().getStreet() : existAddress.getStreet());
        existAddress.setCountry(userBody.getAddress().getCountry() != null ? userBody.getAddress().getCountry() : existAddress.getCountry());
        existAddress.setRegion(userBody.getAddress().getRegion() != null ? userBody.getAddress().getRegion() : existAddress.getRegion());
        existAddress.setBuilding(userBody.getAddress().getBuilding() != null ? userBody.getAddress().getBuilding() : existAddress.getBuilding());
    }

    private boolean areAllFieldsNull(UserBody userBody) {
        Field[] userBodyFields = UserBody.class.getDeclaredFields();
        Field[] addressFields = Address.class.getDeclaredFields();
        List<String> nonNullFields = new ArrayList<>();

        for (Field field : userBodyFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(userBody);
                if (value != null) {
                    if (field.getName().equals("address")) {
                        Address address = (Address) value;
                        if (!areAddressFieldsNull(address, addressFields, nonNullFields)) {
                            return false;
                        }
                    } else {
                        nonNullFields.add(field.getName());
                        return false;
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (nonNullFields.isEmpty()) {
            Map<String, String> fieldsStatus = new HashMap<>();

            for (Field field : userBodyFields) {
                fieldsStatus.put(field.getName(), "data");
            }

            for (Field field : addressFields) {
                fieldsStatus.put(field.getName(), "data");
            }
            throw new InvalidRequestException("Invalid request. The following fields are required:", fieldsStatus);
        }
        return true;
    }

    private boolean areAddressFieldsNull(Address address, Field[] addressFields, List<String> nonNullFields) {
        for (Field field : addressFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(address);
                if (value != null) {
                    nonNullFields.add("address." + field.getName());
                    return false;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            field.setAccessible(false);
        }
        return true;
    }
}

