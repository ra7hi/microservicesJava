package microservices.order_processing.order_service.controllers;

import microservices.order_processing.order_service.controllers.responses.ApiResponse;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        Optional<Users> user = usersService.findUserByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Validated UserDto userDto) {
        usersService.createUser(userDto);
        return ResponseEntity.ok(new ApiResponse(true, "User created successfully!"));
    }

    @PatchMapping("/update/{username}")
    public ResponseEntity<?> updateUserInfo(@PathVariable String username, @RequestBody @Validated UserDto userDto) {
        usersService.updateUser(username, userDto);
        return ResponseEntity.ok(new ApiResponse(true, "User " + username + " has been updated successfully!"));
    }

    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> removeUser(@PathVariable String username) {
        usersService.deleteUser(username);
        return ResponseEntity.ok(new ApiResponse(true, "User " + username + " has been deleted successfully!"));
    }
}
