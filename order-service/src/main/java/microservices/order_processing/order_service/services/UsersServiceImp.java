package microservices.order_processing.order_service.services;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.utils.NullPropertyNames;
import microservices.order_processing.order_service.controllers.requests.RegisterRequest;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.enums.Role;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.exception.RegistrationException;
import microservices.order_processing.order_service.exception.UserNotFoundException;
import microservices.order_processing.order_service.repository.UsersRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class UsersServiceImp implements UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void registerUser(RegisterRequest registerRequest, Set<Role> roles) {
        if (usersRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RegistrationException("Username is already taken!");
        }

        if (usersRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RegistrationException("Email is already in use!");
        }

        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRoles(roles);

        usersRepository.save(user);
    }

    @Override
    public void createUser(UserDto userDto) {
        if (usersRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RegistrationException("Username is already taken!");
        }

        if (usersRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RegistrationException("Email is already in use!");
        }

        Users user = new Users();
        BeanUtils.copyProperties(userDto,user, "password");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        usersRepository.save(user);

    }

    @Override
    public void updateUser(String username, UserDto userDto) {
        Optional<Users> user = usersRepository.findByUsername(username);
        if(user.isPresent()) {
            if (usersRepository.findByUsername(userDto.getUsername()).isPresent() &&
                    !user.get().getUsername().equals(userDto.getUsername())) {
                throw new RegistrationException("Username is already taken!");
            }
            if(usersRepository.findByEmail(userDto.getEmail()).isPresent() &&
                    !user.get().getEmail().equals(userDto.getEmail())) {
                throw new RegistrationException("Email is already in use!");
            }
        }
        else{
            throw new UserNotFoundException("User not found!");
        }
        BeanUtils.copyProperties(userDto,user,
                NullPropertyNames.getNullPropertyNames(userDto));
        usersRepository.save(user.get());
    }

    @Override
    public void deleteUser(String username) {
        Optional<Users> user = usersRepository.findByUsername(username);
        if(user.isPresent()) {
            usersRepository.delete(user.get());
        }
        else{
            throw new UserNotFoundException("User not found!");
        }
    }

    @Override
    public Optional<Users> findUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    @Override
    public Optional<Users> findUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}
