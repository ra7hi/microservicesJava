package microservices.order_processing.order_service.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

/**
 * Утилитный класс для работы с JWT (JSON Web Token).
 * <p>
 * Основные функции:
 * <ul>
 *     <li>Создание JWT с пользовательскими данными и ролями.</li>
 *     <li>Извлечение имени пользователя из JWT.</li>
 *     <li>Валидация JWT на корректность и срок действия.</li>
 *     <li>Генерация секретного ключа на основе конфигурации.</li>
 * </ul>
 */
@Component
@Slf4j
public class JwtUtils {
    /**
     * Секретный ключ для подписи JWT в формате base64.
     * Загружается из конфигурации приложения.
     */
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    /**
     * Время жизни JWT в миллисекундах.
     * Загружается из конфигурации приложения.
     */
    @Value("${app.jwt-expiration-ms}")
    private int jwtExpirationMs;

    /**
     * Создаёт JWT для аутентифицированного пользователя.
     *
     * @param authentication объект аутентификации, содержащий данные пользователя
     * @return сформированный JWT в виде строки
     */
    public String buildToken(Authentication authentication) {
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        Date expiration = new Date(System.currentTimeMillis() + jwtExpirationMs);

        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Извлекает username из JWT.
     *
     * @param token JWT в виде строки
     * @return имя пользователя, содержащееся в токене
     */
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Валидирует JWT: проверяет корректность подписи и срок действия.
     *
     * @param token JWT в виде строки
     * @return {@code true}, если токен валиден; {@code false} — если нет
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Получает секретный ключ для подписи и проверки JWT.
     *
     * @return секретный ключ типа {@link SecretKey}
     */
    public SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
