package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.responses.RefreshTokenResponse;
import microservices.order_processing.order_service.exception.InvalidTokenException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import microservices.order_processing.order_service.controllers.requests.LoginRequest;
import microservices.order_processing.order_service.controllers.responses.JwtAuthenticationResponse;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import microservices.order_processing.order_service.jwt.JwtUtils;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImpTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private CustomUserDetailsServiceImp customUserDetailsServiceImp;

    @InjectMocks
    private AuthServiceImp authService;

    @Test
    void authenticateUserSuccess() {
        LoginRequest loginRequest = new LoginRequest("user", "password");
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = mock(UserDetailsImpl.class);

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);
        when(authentication.getPrincipal())
                .thenReturn(userDetails);
        when(jwtUtils.buildToken(authentication))
                .thenReturn("fake-jwt-token");

        JwtAuthenticationResponse response = authService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getAccessToken());
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("user", "password")
        );
        verify(jwtUtils).buildToken(authentication);
    }

    @Test
    void refreshAccessTokenValidTokenSuccess() {
        String refreshToken = "valid-refresh-token";
        UserDetails userDetails = mock(UserDetails.class);

        when(jwtUtils.validateToken(refreshToken))
                .thenReturn(true);
        when(jwtUtils.getUsernameFromToken(refreshToken))
                .thenReturn("user");
        when(customUserDetailsServiceImp.loadUserByUsername("user"))
                .thenReturn(userDetails);
        when(jwtUtils.buildToken(any()))
                .thenReturn("new-access-token");

        RefreshTokenResponse response = authService.refreshAccessToken(refreshToken);

        assertEquals("new-access-token", response.getAccessToken());
        verify(jwtUtils).validateToken(refreshToken);
    }

    @Test
    void refreshAccessTokenInvalidTokenThrowsException() {
        String invalidToken = "invalid-token";
        when(jwtUtils.validateToken(invalidToken))
                .thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> {
            authService.refreshAccessToken(invalidToken);
        });
    }
}
