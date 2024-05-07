package com.kislun.cstest.user.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kislun.cstest.user.dto.UserBody;
import com.kislun.cstest.user.model.LocalUser;
import com.kislun.cstest.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetAllUsers() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.getAllUsers(pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new LocalUser())));

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById() throws Exception {
        UUID userId = UUID.randomUUID();
        LocalUser user = new LocalUser();
        user.setId(userId);
        when(userService.getUserById(userId)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/{id}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateUser() throws Exception {
        UserBody userBody = new UserBody();
        userBody.setFirstName("updatedFirstName");
        userBody.setLastName("updatedLastName");
        userBody.setBirthDate(LocalDate.of(2000, 1, 1));
        userBody.setEmail("updatedEmail@mail.com");
        LocalUser user = new LocalUser();
        when(userService.createUser(any(UserBody.class))).thenReturn(user);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody)))
                .andExpect(status().isCreated());
    }

    @Test
    void testDeleteUser() throws Exception {
        UUID userId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void testUpdateUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserBody userBody = new UserBody();
        userBody.setFirstName("updatedFirstName");
        userBody.setLastName("updatedLastName");
        userBody.setBirthDate(LocalDate.of(2000, 1, 1));
        userBody.setEmail("updatedEmail@mail.com");
        LocalUser updatedUser = new LocalUser();
        when(userService.getUserById(userId)).thenReturn(Optional.of(new LocalUser()));
        when(userService.updateUser(any(LocalUser.class), any(UserBody.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody)))
                .andExpect(status().isOk());
    }

    @Test
    void testPatchUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserBody userBody = new UserBody();
        LocalUser patchedUser = new LocalUser();
        when(userService.getUserById(userId)).thenReturn(Optional.of(new LocalUser()));
        when(userService.patchUser(any(LocalUser.class), any(UserBody.class))).thenReturn(patchedUser);

        mockMvc.perform(patch("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody)))
                .andExpect(status().isOk());
    }

    @Test
    void testSearchBetweenDate() throws Exception {
        LocalDate from = LocalDate.of(2023, 1, 1);
        LocalDate to = LocalDate.of(2023, 12, 31);
        Pageable pageable = PageRequest.of(0, 10);
        when(userService.searchBetweenDate(from, to, pageable))
                .thenReturn(new PageImpl<>(Collections.singletonList(new LocalUser())));

        mockMvc.perform(get("/api/v1/users/search")
                        .param("start", from.toString())
                        .param("end", to.toString())
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}