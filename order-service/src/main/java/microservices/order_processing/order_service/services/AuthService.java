package microservices.order_processing.order_service.services;

import microservices.order_processing.order_service.controllers.requests.LoginRequest;
import microservices.order_processing.order_service.controllers.responses.JwtAuthenticationResponse;
import microservices.order_processing.order_service.controllers.responses.RefreshTokenResponse;

public interface AuthService {
    JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);
    RefreshTokenResponse refreshAccessToken(String refreshToken);
}
