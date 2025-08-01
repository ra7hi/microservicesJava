package microservices.order_processing.order_service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import microservices.order_processing.order_service.entities.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Реализация интерфейса {@link UserDetails} для интеграции сущности {@link microservices.order_processing.order_service.entities.Users}
 * с механизмом аутентификации Spring Security.
 */
@AllArgsConstructor
@Getter
public class UserDetailsImpl implements UserDetails {

    private Users user;

    /**
     * Возвращает список ролей пользователя в виде коллекции {@link GrantedAuthority}.
     * Каждая роль преобразуется в формат "ROLE_{имя_роли}".
     *
     * @return список ролей пользователя
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getAuthority()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает зашифрованный пароль пользователя.
     *
     * @return пароль пользователя
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Возвращает уникальное имя пользователя.
     *
     * @return имя пользователя
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }
}
