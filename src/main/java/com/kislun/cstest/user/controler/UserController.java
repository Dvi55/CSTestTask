package com.kislun.cstest.user.controler;

import com.kislun.cstest.user.mapper.UserBody;
import com.kislun.cstest.user.model.LocalUser;
import com.kislun.cstest.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<LocalUser>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalUser> getById(@PathVariable("id") UUID id) {
        Optional<LocalUser> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<LocalUser> createUser1(@Valid @RequestBody UserBody userBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userBody));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalUser> updateUser(@PathVariable("id") UUID id, @Valid @RequestBody UserBody userBody) {
        Optional<LocalUser> updatedUser = userService.getUserById(id);
        if (updatedUser.isPresent()) {
            return updatedUser.map(u -> ResponseEntity.ok(userService.updateUser(updatedUser.get(), userBody)))
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PatchMapping("/{id}")
    public ResponseEntity<LocalUser> patchUser(@PathVariable("id") UUID id, @Valid @RequestBody UserBody userBody) {
        Optional<LocalUser> user = userService.getUserById(id);
        return user.map(localUser -> ResponseEntity.ok(userService.patchUser(localUser))).
                orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBetweenDate(@RequestParam("start")
                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                             @RequestParam("end")
                                                             @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate to) {
        if (from.isBefore(to)) {
            return ResponseEntity.ok(userService.searchBetweenDate(from, to));
        } else {
            return ResponseEntity.badRequest().body("Start date must be before end date");
        }
    }
}