package com.kislun.cstest.user.service;

import com.kislun.cstest.user.dao.LocalUserDAO;
import com.kislun.cstest.user.dto.UserBody;
import com.kislun.cstest.user.mapper.UserMapper;
import com.kislun.cstest.user.model.Address;
import com.kislun.cstest.user.model.LocalUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private LocalUserDAO localUserDAO;

    @Mock
    private UserMapper userMapper;

    private UserService userService;

    private LocalUser expectedUser;
    private LocalUser user1;
    private List<LocalUser> expectedUsers;
    private Address address;

    @BeforeEach
    void setUp() {
        userService = new UserService(localUserDAO, userMapper);
        when(userMapper.mapToLocalUser(any(UserBody.class)))
                .thenAnswer(invocation -> {
                    UserBody userBody = invocation.getArgument(0);
                    LocalUser localUser = new LocalUser();
                    localUser.setBirthDate(userBody.getBirthDate());
                    localUser.setFirstName(userBody.getFirstName());
                    localUser.setLastName(userBody.getLastName());
                    localUser.setAddress(userBody.getAddress());
                    localUser.setPhoneNumber(userBody.getPhoneNumber());
                    localUser.setEmail(userBody.getEmail());
                    return localUser;
                });

        doAnswer(invocation -> {
            UserBody userBody = invocation.getArgument(0);
            LocalUser user = invocation.getArgument(1);
            user.setFirstName(userBody.getFirstName());
            user.setLastName(userBody.getLastName());
            user.setBirthDate(userBody.getBirthDate());
            user.setAddress(userBody.getAddress());
            user.setPhoneNumber(userBody.getPhoneNumber());
            user.setEmail(userBody.getEmail());
            return null;
        }).when(userMapper).updateUserFromBody(any(UserBody.class), any(LocalUser.class));
        address = new Address();
        address.setStreet("123 Main St");
        address.setCity("Anytown");
        address.setRegion("CA");
        address.setBuilding("12");
        UUID userId = UUID.randomUUID();
        expectedUser = new LocalUser();
        expectedUser.setId(userId);
        expectedUser.setFirstName("John");
        expectedUser.setLastName("Doe");
        expectedUser.setAddress(address);
        user1 = new LocalUser();
        user1.setId(UUID.randomUUID());
        user1.setFirstName("Jonatan");
        user1.setBirthDate(LocalDate.of(1990, 1, 1));
        LocalUser user2 = new LocalUser();
        user2.setId(UUID.randomUUID());
        user2.setFirstName("Jane");
        user2.setBirthDate(LocalDate.of(2000, 1, 1));
        expectedUsers = new ArrayList<>();
        expectedUsers.add(user1);
        expectedUsers.add(user2);
        when(localUserDAO.findById(any(UUID.class))).thenReturn(Optional.of(expectedUser));
    }


    @Test
    void testGetUserById() {
        Optional<LocalUser> result = userService.getUserById(expectedUser.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(expectedUser.getId(), result.get().getId());
    }

    @Test
    void getUserById_UserNotExists() {
        UUID userId = UUID.randomUUID();
        when(localUserDAO.findById(userId)).thenReturn(Optional.empty());
        Optional<LocalUser> result = userService.getUserById(userId);
        assertEquals(Optional.empty(), result);
    }

    @Test
    void getAllUsers() {
            Page<LocalUser> expectedPage = new PageImpl<>(expectedUsers);

            when(localUserDAO.findAll(any(Pageable.class))).thenReturn(expectedPage);

            Page<LocalUser> result = userService.getAllUsers(PageRequest.of(0, 10));

            assertEquals(expectedPage, result);
        }

    @Test
    void testGetAllUsers_ReturnsExpectedUsers() {
        Page<LocalUser> expectedPage = new PageImpl<>(expectedUsers);

        when(localUserDAO.findAll(any(Pageable.class))).thenReturn(expectedPage);

        Page<LocalUser> result = userService.getAllUsers(PageRequest.of(0, 10));

        assertEquals(2, result.getContent().size());
    }


    @Test
    void testCreateUser() {
        UserBody validUserBody = new UserBody();

        LocalUser expectedUser = new LocalUser();

        when(userMapper.mapToLocalUser(validUserBody)).thenReturn(expectedUser);
        when(localUserDAO.save(expectedUser)).thenReturn(expectedUser);

        LocalUser actualUser = userService.createUser(validUserBody);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void deleteUserById() {
        UUID userId = UUID.randomUUID();

        userService.deleteUserById(userId);

        verify(localUserDAO, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserById_InvalidUserId() {
        UUID userId = UUID.randomUUID();

        doThrow(new IllegalArgumentException()).when(localUserDAO).deleteById(userId);

        assertThrows(IllegalArgumentException.class, () -> userService.deleteUserById(userId));
    }



    @Test
    void updateUserTest() {
        LocalUser user = new LocalUser();
        UserBody userBody = new UserBody();
        userBody.setFirstName("John");
        userBody.setLastName("Doe");
        userBody.setBirthDate(LocalDate.of(1990, 1, 1));
        userBody.setPhoneNumber("1234567890");
        userBody.setEmail("john.doe@example.com");

        userBody.setAddress(address);


        when(localUserDAO.save(user)).thenReturn(user);

        LocalUser actualUser = userService.updateUser(user, userBody);
        assertEquals("John", actualUser.getFirstName());
        assertEquals("Doe", actualUser.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), actualUser.getBirthDate());
        assertEquals("1234567890", actualUser.getPhoneNumber());
        assertEquals("john.doe@example.com", actualUser.getEmail());
        assertEquals("123 Main St", actualUser.getAddress().getStreet());
        assertEquals("Anytown", actualUser.getAddress().getCity());
        assertEquals("CA", actualUser.getAddress().getRegion());
    }

    @Test
    void searchBetweenDate() {
        LocalDate from = LocalDate.of(1989, 1, 1);
        LocalDate to = LocalDate.of(2001, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);
        Page<LocalUser> expectedPage = new PageImpl<>(expectedUsers);

        when(localUserDAO.findByBirthDateBetween(from, to, pageable)).thenReturn(expectedPage);
        Page<LocalUser> result = userService.searchBetweenDate(from, to, pageable);
        assertEquals(expectedPage, result);
        assertEquals(2, result.getContent().size());
        assertTrue(result.getContent().containsAll(expectedUsers));
    }

    @Test
    void patchUser() {
        UserBody userBody = new UserBody();
        userBody.setFirstName("Jane");
        userBody.setLastName("Smith");
        userBody.setBirthDate(LocalDate.of(1985, 10, 20));
        userBody.setPhoneNumber("+380987654321");
        userBody.setEmail("jane.smith@example.com");
        Address newAddress = new Address();
        newAddress.setCountry("Canada");
        newAddress.setCity("Toronto");
        newAddress.setRegion("Ontario");
        newAddress.setStreet("Queen Street");
        newAddress.setBuilding("456");
        userBody.setAddress(newAddress);

        LocalUser expectedUser = new LocalUser();
        expectedUser.setId(user1.getId());
        expectedUser.setFirstName("Jane");
        expectedUser.setLastName("Smith");
        expectedUser.setBirthDate(LocalDate.of(1985, 10, 20));
        expectedUser.setPhoneNumber("+380987654321");
        expectedUser.setEmail("jane.smith@example.com");
        Address expectedAddress = new Address();
        expectedAddress.setCountry("Canada");
        expectedAddress.setCity("Toronto");
        expectedAddress.setRegion("Ontario");
        expectedAddress.setStreet("Queen Street");
        expectedAddress.setBuilding("456");
        expectedUser.setAddress(expectedAddress);

        when(localUserDAO.save(any(LocalUser.class))).thenReturn(expectedUser);

        // Act
        LocalUser result = userService.patchUser(user1, userBody);

        // Assert
        assertEquals(expectedUser, result);
    }
}