package microservices.order_processing.order_service.services;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.impl.UserDetailsImpl;
import microservices.order_processing.order_service.repository.UsersRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис загрузки деталей пользователя по имени пользователя.
 * Реализация интерфейса {@link UserDetailsService} для интеграции с Spring Security.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImp implements UserDetailsService {

    private final UsersRepository usersRepository;

    /**
     * Загружает пользователя по имени пользователя.
     * Выполняет поиск пользователя в репозитории, если пользователь не найден, выбрасывается исключение {@link UsernameNotFoundException}.
     * Возвращает объект {@link UserDetailsImpl}, содержащий данные пользователя для Spring Security.
     *
     * @param username имя пользователя
     * @return объект UserDetails с информацией о пользователе
     * @throws UsernameNotFoundException если пользователь с указанным именем не найден
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

        return new UserDetailsImpl(user);
    }
}
