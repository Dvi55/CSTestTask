package com.kislun.cstest.user.controler;

import com.kislun.cstest.user.UserNotFoundException;
import com.kislun.cstest.user.dto.UserBody;
import com.kislun.cstest.user.model.LocalUser;
import com.kislun.cstest.user.service.UserService;
import com.kislun.cstest.validation.group.OnPatch;
import jakarta.validation.groups.Default;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    public ResponseEntity<Page<LocalUser>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocalUser> getById(@PathVariable("id") UUID id) {
        Optional<LocalUser> user = userService.getUserById(id);
        return user.map(ResponseEntity::ok).orElseThrow(() -> new UserNotFoundException(id));
    }

    @PostMapping()
    public ResponseEntity<LocalUser> createUser(@Validated(Default.class) @RequestBody UserBody userBody) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userBody));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocalUser> updateUser(@PathVariable("id") UUID id, @Validated(Default.class) @RequestBody UserBody userBody) {
        Optional<LocalUser> updatedUser = userService.getUserById(id);
        return updatedUser.map(u -> ResponseEntity.ok(userService.updateUser(updatedUser.get(), userBody)))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LocalUser> patchUser(@PathVariable("id") UUID id, @Validated(OnPatch.class) @RequestBody UserBody userBody) {
        Optional<LocalUser> user = userService.getUserById(id);
        return user.map(localUser -> ResponseEntity.ok(userService.patchUser(localUser, userBody))).
                orElseThrow(() -> new UserNotFoundException(id));
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchBetweenDate(@RequestParam("start")
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                    @RequestParam("end")
                                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
                                                    Pageable pageable) {
        if (from.isBefore(to)) {
            return ResponseEntity.ok(userService.searchBetweenDate(from, to, pageable));
        } else {
            return ResponseEntity.badRequest().body("Start date must be before end date");
        }
    }
}