package microservices.order_processing.order_service;


import microservices.order_processing.order_service.controllers.requests.RegisterRequest;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.enums.Role;
import microservices.order_processing.order_service.exception.RegistrationException;
import microservices.order_processing.order_service.exception.UserNotFoundException;
import microservices.order_processing.order_service.repository.UsersRepository;
import microservices.order_processing.order_service.services.UsersServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.GeneralSecurityException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UsersServiceImpTest {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsersServiceImp usersService;

    @Test
    public void registerUser_Success() throws GeneralSecurityException {
        RegisterRequest request = new RegisterRequest("user", "email@test.com", "password");
        Set<Role> roles = Set.of(Role.USER);

        when(usersRepository.findByUsername("user"))
                .thenReturn(Optional.empty());
        when(usersRepository.findByEmail("email@test.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        usersService.registerUser(request, roles);

        verify(usersRepository).save(argThat(user ->
                user.getUsername().equals("user") &&
                        user.getEmail().equals("email@test.com") &&
                        user.getPassword().equals("encodedPassword") &&
                        user.getRoles().equals(Set.of(Role.USER))
        ));
    }

    @Test
    public void registerUser_UsernameTaken_ThrowsException() {
        RegisterRequest request = new RegisterRequest("taken", "email@test.com", "pass");
        when(usersRepository.findByUsername("taken"))
                .thenReturn(Optional.of(new Users()));

        assertThatThrownBy(() -> usersService.registerUser(request, Set.of(Role.USER)))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Username is already taken!");
    }

    @Test
    public void registerUser_EmailTaken_ThrowsException() {
        RegisterRequest request = new RegisterRequest("user", "taken@test.com", "pass");
        when(usersRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(new Users()));

        assertThatThrownBy(() -> usersService.registerUser(request, Set.of(Role.USER)))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Email is already in use!");
    }

    @Test
    public void createUser_Success() throws GeneralSecurityException {
        UserDto userDto = new UserDto("newUser", "new@email.com", "password", Set.of(Role.ADMIN));

        when(usersRepository.findByUsername("newUser"))
                .thenReturn(Optional.empty());
        when(usersRepository.findByEmail("new@email.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPass");

        usersService.createUser(userDto);

        verify(usersRepository).save(argThat(user ->
                user.getUsername().equals("newUser") &&
                        user.getEmail().equals("new@email.com") &&
                        user.getPassword().equals("encodedPass") &&
                        user.getRoles().equals(Set.of(Role.ADMIN))
        ));
    }

    @Test
    public void createUser_DuplicateEmail_ThrowsException() {
        UserDto userDto = new UserDto("user", "taken@email.com", "pass", Set.of(Role.USER));
        when(usersRepository.findByUsername("user")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("taken@email.com")).thenReturn(Optional.of(new Users()));

        assertThatThrownBy(() -> usersService.createUser(userDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Email is already in use!");
    }

    @Test
    public void updateUser_Success() {
        Users existingUser = new Users();
        existingUser.setUsername("oldUser");
        existingUser.setEmail("old@email.com");
        existingUser.setPassword("oldPass");

        UserDto updateDto = new UserDto("newUser", "new@email.com", null, Set.of(Role.ADMIN));

        when(usersRepository.findByUsername("oldUser")).thenReturn(Optional.of(existingUser));
        when(usersRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(usersRepository.findByEmail("new@email.com")).thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users newExistingUser =  usersService.updateUser("oldUser", updateDto);

        assertThat(newExistingUser.getUsername()).isEqualTo("newUser");
        assertThat(newExistingUser.getEmail()).isEqualTo("new@email.com");
        assertThat(newExistingUser.getPassword()).isEqualTo("oldPass");
        assertThat(newExistingUser.getRoles()).isEqualTo(Set.of(Role.ADMIN));
        verify(usersRepository).save(existingUser);
    }

    @Test
    public void updateUser_PartialUpdate_Success() {
        Users existingUser = new Users();
        existingUser.setUsername("oldUser");
        existingUser.setEmail("old@email.com");
        existingUser.setPassword("oldPass");

        UserDto partialUpdate = new UserDto();
        partialUpdate.setEmail("new@email.com");

        when(usersRepository.findByUsername("oldUser"))
                .thenReturn(Optional.of(existingUser));
        when(usersRepository.findByEmail("new@email.com"))
                .thenReturn(Optional.empty());
        when(usersRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Users newExistingUser = usersService.updateUser("oldUser", partialUpdate);

        assertThat(newExistingUser.getEmail()).isEqualTo("new@email.com");
        assertThat(newExistingUser.getUsername()).isEqualTo("oldUser");
        assertThat(newExistingUser.getPassword()).isEqualTo("oldPass");
        verify(usersRepository).save(existingUser);
    }


    @Test
    public void updateUser_UserNotFound_ThrowsException() {
        UserDto updateDto = new UserDto();
        when(usersRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.updateUser("unknown", updateDto))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }

    @Test
    public void updateUser_DuplicateUsername_ThrowsException() {
        Users existingUser = new Users();
        existingUser.setUsername("user1");
        existingUser.setEmail("user1@test.com");

        Users anotherUser = new Users();
        anotherUser.setUsername("user2");

        UserDto updateDto = new UserDto("user2", "user1@test.com", null, null);

        when(usersRepository.findByUsername("user1"))
                .thenReturn(Optional.of(existingUser));
        when(usersRepository.findByUsername("user2"))
                .thenReturn(Optional.of(anotherUser)); // Имя занято другим пользователем

        assertThatThrownBy(() -> usersService.updateUser("user1", updateDto))
                .isInstanceOf(RegistrationException.class)
                .hasMessage("Username is already taken!");
    }

    @Test
    public void deleteUser_Success() {
        Users user = new Users();
        when(usersRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        usersService.deleteUser("user");

        verify(usersRepository).delete(user);
    }

    @Test
    public void deleteUser_UserNotFound_ThrowsException() {
        when(usersRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usersService.deleteUser("unknown"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found!");
    }
}
