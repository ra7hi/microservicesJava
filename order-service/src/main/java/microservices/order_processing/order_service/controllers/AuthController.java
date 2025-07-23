package microservices.order_processing.order_service.controllers;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.controllers.requests.RefreshTokenRequest;
import microservices.order_processing.order_service.controllers.responses.ApiResponse;
import microservices.order_processing.order_service.controllers.responses.JwtAuthenticationResponse;
import microservices.order_processing.order_service.controllers.requests.LoginRequest;
import microservices.order_processing.order_service.controllers.requests.RegisterRequest;
import microservices.order_processing.order_service.controllers.responses.RefreshTokenResponse;
import microservices.order_processing.order_service.enums.Role;
import microservices.order_processing.order_service.services.AuthServiceImp;
import microservices.order_processing.order_service.services.UsersServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsersServiceImp usersServiceImp;
    private final AuthServiceImp authServiceImp;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody RegisterRequest registerRequest) {
        usersServiceImp.registerUser(registerRequest, Set.of(Role.USER));
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Validated @RequestBody LoginRequest loginRequest) {
        JwtAuthenticationResponse jwtAuthenticationResponse = authServiceImp.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authServiceImp.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
