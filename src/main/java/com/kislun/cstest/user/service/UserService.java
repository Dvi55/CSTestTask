package com.kislun.cstest.user.service;

import com.kislun.cstest.user.InvalidRequestException;
import com.kislun.cstest.user.dao.LocalUserDAO;
import com.kislun.cstest.user.dto.UserBody;
import com.kislun.cstest.user.mapper.UserMapper;
import com.kislun.cstest.user.model.LocalUser;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.SneakyThrows;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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

    public LocalUser patchUser(LocalUser user, UserBody userBody) throws InvalidRequestException {
        return localUserDAO.save(userPatcher(user, userBody));
    }

    @SneakyThrows
    public LocalUser userPatcher(LocalUser user, UserBody userBody) throws InvalidRequestException {
        Field[] userFields = LocalUser.class.getDeclaredFields();
        Set<String> validFields = Arrays.stream(userFields)
                .map(Field::getName)
                .filter(fieldName -> !fieldName.equals("id"))
                .collect(Collectors.toSet());

        Field[] userBodyFields = UserBody.class.getDeclaredFields();
        boolean allFieldsNull = true;
        for (Field field : userBodyFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (fieldName.equals("id")) {
                continue;
            }

            if (!validFields.contains(fieldName)) {
                throw new InvalidRequestException("Invalid field: " + fieldName + ". Valid fields are: " + String.join(", ", validFields));
            }

            Object value = field.get(userBody);
            if (value != null) {
                allFieldsNull = false;
                try {
                    Field correspondingField = LocalUser.class.getDeclaredField(fieldName);
                    correspondingField.setAccessible(true);
                    if (correspondingField.getType().equals(String.class) && field.getType().equals(String.class)) {
                        String stringValue = (String) value;
                        if (isValidString(stringValue, field)) {
                            correspondingField.set(user, stringValue);
                        } else {
                            throw new InvalidRequestException("Invalid value for field '" + fieldName + "': " + stringValue);
                        }
                    } else if (correspondingField.getType().equals(LocalDate.class) && field.getType().equals(LocalDate.class)) {
                        LocalDate birthDate = (LocalDate) value;
                        try {
                            birthDate = LocalDate.parse(birthDate.toString(), DateTimeFormatter.ISO_DATE);
                            correspondingField.set(user, birthDate);
                        } catch (DateTimeParseException e) {
                            throw new DateTimeParseException("Invalid date format for field '" + fieldName + "': " + birthDate, e.getParsedString(), e.getErrorIndex());
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }

        if (allFieldsNull) {
            throw new InvalidRequestException("No fields provided. Valid fields are: " + String.join(", ", validFields));
        }

        return user;
    }

    private boolean isValidString(String value, Field field) {
        field.setAccessible(true);
        Object[] annotations = field.getAnnotations();
        for (Object annotation : annotations) {
            if (annotation instanceof NotBlank) {
                if (value.isEmpty()) {
                    return false;
                }
            } else if (annotation instanceof Length) {
                Length lengthAnnotation = (Length) annotation;
                int min = lengthAnnotation.min();
                int max = lengthAnnotation.max();
                if (value.length() < min || value.length() > max) {
                    return false;
                }
            } else if (annotation instanceof Pattern) {
                Pattern patternAnnotation = (Pattern) annotation;
                if (!value.matches(patternAnnotation.regexp())) {
                    return false;
                }
            }
        }
        return true;
    }
}
