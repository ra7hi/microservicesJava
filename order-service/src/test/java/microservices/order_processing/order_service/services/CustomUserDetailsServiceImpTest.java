package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.enums.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.repository.UsersRepository;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImpTest {

    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private CustomUserDetailsServiceImp userDetailsService;

    @Test
    void loadUserByUsernameUserExistsReturnsUserDetails() {
        String username = "testUser";
        Users mockUser = new Users();
        mockUser.setUsername(username);
        mockUser.setPassword("encodedPassword");
        mockUser.setRoles(Set.of(Role.USER));

        when(usersRepository.findByUsername(username))
                .thenReturn(Optional.of(mockUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals("encodedPassword", userDetails.getPassword());
        verify(usersRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsernameUserNotFoundThrowsException() {
        String nonExistentUsername = "nonExistentUser";
        when(usersRepository.findByUsername(nonExistentUsername))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(nonExistentUsername);
        });

        verify(usersRepository).findByUsername(nonExistentUsername);
    }
}
