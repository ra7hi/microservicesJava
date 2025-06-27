package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.config.NullPropertyNames;
import microservices.order_processing.order_service.controllers.requests.RegisterRequest;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.entities.Role;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.repository.UsersRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional
public class UsersService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final NullPropertyNames nullPropertyNames;

    @Autowired
    public UsersService(UsersRepository usersRepository, PasswordEncoder passwordEncoder, NullPropertyNames nullPropertyNames) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.nullPropertyNames = nullPropertyNames;
    }

    public void registerUser(RegisterRequest registerRequest, Set<Role> roles) {
        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRoles(roles);

        usersRepository.save(user);
    }

    public void createUser(UserDto userDto) {
        Users user = new Users();
        BeanUtils.copyProperties(userDto,user, "password");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        usersRepository.save(user);

    }

    public void updateUser(Users user, UserDto userDto) {
        BeanUtils.copyProperties(userDto,user,
                nullPropertyNames.getNullPropertyNames(userDto));
        usersRepository.save(user);
    }

    public boolean isUsernameTaken(String username) {
        return usersRepository.findByUsername(username).isPresent();
    }

    public boolean isEmailTaken(String email) {
        return usersRepository.findByEmail(email).isPresent();
    }
}
