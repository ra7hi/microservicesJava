package microservices.order_processing.order_service.controllers;

import microservices.order_processing.order_service.controllers.responses.ApiResponse;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.repository.UsersRepository;
import microservices.order_processing.order_service.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersRepository usersRepository;
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersRepository usersRepository, UsersService usersService) {
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserInfo(@PathVariable String username) {
        Optional<Users> user = usersRepository.findByUsername(username);
        if (user.isPresent()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Validated UserDto userDto) {
        if(usersService.isUsernameTaken(userDto.getUsername())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Username is already taken!"));
        }
        if(usersService.isEmailTaken(userDto.getEmail())) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Email is already in use!"));
        }
        usersService.createUser(userDto);
        return ResponseEntity.ok(new ApiResponse(true, "User created successfully!"));
    }

    @PatchMapping("/update/{username}")
    public ResponseEntity<?> updateUserInfo(@PathVariable String username, @RequestBody @Validated UserDto userDto) {
        Users user = usersRepository.findByUsername(username).orElse(null);
        if(user != null) {
            if (usersRepository.findByUsername(userDto.getUsername()).isPresent() &&
                    !user.getUsername().equals(userDto.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Username is already taken!"));
            }
            if(usersRepository.findByEmail(userDto.getEmail()).isPresent() &&
                    !user.getEmail().equals(userDto.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Email is already in use!"));
            }
            usersService.updateUser(user, userDto);
            return ResponseEntity.ok(new ApiResponse(true, "User " + username + " has been updated successfully!"));
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
    }

    @DeleteMapping("/remove/{username}")
    public ResponseEntity<?> removeUser(@PathVariable String username) {
        Users user = usersRepository.findByUsername(username).orElse(null);
        if(user != null) {
            usersRepository.delete(user);
            return ResponseEntity.ok(new ApiResponse(true, "User " + username + " has been deleted successfully!"));
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "User not found!"));
        }
    }
}
