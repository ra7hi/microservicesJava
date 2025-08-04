package microservices.order_processing.order_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

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

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API для регистрации, входа и обновления токенов пользователей")
public class AuthController {
    private final UsersServiceImp usersServiceImp;
    private final AuthServiceImp authServiceImp;

    @Operation(
            summary = "Регистрация пользователя",
            description = "Регистрирует нового пользователя с ролью USER."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для регистрации пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @Validated @org.springframework.web.bind.annotation.RequestBody RegisterRequest registerRequest) {

        usersServiceImp.registerUser(registerRequest, Set.of(Role.USER));
        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully!"));
    }

    @Operation(
            summary = "Авторизация пользователя",
            description = "Проверяет учетные данные пользователя и возвращает JWT-токен."
    )
    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> loginUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для входа пользователя",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Validated @org.springframework.web.bind.annotation.RequestBody LoginRequest loginRequest) {

        JwtAuthenticationResponse jwtAuthenticationResponse = authServiceImp.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtAuthenticationResponse);
    }

    @Operation(
            summary = "Обновление access-токена",
            description = "Обновляет JWT access-токен с помощью refresh-токена."
    )
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh токен для обновления access токена",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody RefreshTokenRequest request) {

        RefreshTokenResponse response = authServiceImp.refreshAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}
