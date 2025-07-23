package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.requests.RegisterRequest;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.enums.Role;
import microservices.order_processing.order_service.entities.Users;

import java.util.Optional;
import java.util.Set;

public interface UserService {
    void registerUser(RegisterRequest registerRequest, Set<Role> roles);
    void createUser(UserDto userDto);
    void updateUser(String username, UserDto userDto);
    void deleteUser(String username);
    Optional<Users> findUserByUsername(String username);
    Optional<Users> findUserByEmail(String email);
}
