package microservices.order_processing.order_service.services;

import lombok.RequiredArgsConstructor;
import microservices.order_processing.order_service.utils.NullPropertyNames;
import microservices.order_processing.order_service.controllers.requests.RegisterRequest;
import microservices.order_processing.order_service.dto.UserDto;
import microservices.order_processing.order_service.enums.Role;
import microservices.order_processing.order_service.entities.Users;
import microservices.order_processing.order_service.exception.RegistrationException;
import microservices.order_processing.order_service.exception.UserNotFoundException;
import microservices.order_processing.order_service.repository.UsersRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

/**
 * Реализация интерфейса {@link UserService}:
 * регистрация, создание, обновление, удаление, а также поиск по имени пользователя и email.
 *
 * <p>Все методы помечены транзакционными аннотациями для обеспечения согласованности данных.</p>
 */
@Service
@Transactional
@RequiredArgsConstructor
public class UsersServiceImp implements UserService {
    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Регистрирует нового пользователя на основе регистрационного запроса и заданных ролей.
     *
     * @param registerRequest объект, содержащий данные для регистрации (имя пользователя, email, пароль)
     * @param roles набор ролей, назначаемых пользователю
     * @throws RegistrationException если имя пользователя или email уже заняты
     */
    @Override
    public void registerUser(RegisterRequest registerRequest, Set<Role> roles) {
        if (usersRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RegistrationException("Username is already taken!");
        }

        if (usersRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RegistrationException("Email is already in use!");
        }

        Users user = new Users();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRoles(roles);

        usersRepository.save(user);
    }

    /**
     * Создает нового пользователя на основе DTO. Пароль будет зашифрован.
     *
     * @param userDto DTO с данными пользователя
     * @throws RegistrationException если имя пользователя или email уже заняты
     */
    @Override
    public void createUser(UserDto userDto) {
        if (usersRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RegistrationException("Username is already taken!");
        }

        if (usersRepository.findByEmail(userDto.getEmail()).isPresent()) {
            throw new RegistrationException("Email is already in use!");
        }

        Users user = new Users();
        BeanUtils.copyProperties(userDto,user, "password");
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        usersRepository.save(user);

    }

    /**
     * Обновляет существующего пользователя, найденного по имени пользователя.
     * При этом игнорируются {@code null}-поля в DTO.
     *
     * @param username имя пользователя, чьи данные нужно обновить
     * @param userDto DTO с новыми данными пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws RegistrationException если новое имя пользователя или email уже заняты другими пользователями
     */
    @Override
    public Users updateUser(String username, UserDto userDto) {
        Users existingUser = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (userDto.getUsername() != null &&
                usersRepository.findByUsername(userDto.getUsername()).isPresent() &&
                !existingUser.getUsername().equals(userDto.getUsername())) {
            throw new RegistrationException("Username is already taken!");
        }

        if (userDto.getEmail() != null &&
                usersRepository.findByEmail(userDto.getEmail()).isPresent() &&
                !existingUser.getEmail().equals(userDto.getEmail())) {
            throw new RegistrationException("Email is already in use!");
        }

        // Вот тут было user — стало existingUser
        BeanUtils.copyProperties(userDto, existingUser,
                NullPropertyNames.getNullPropertyNames(userDto));

        return usersRepository.save(existingUser);
    }

    /**
     * Удаляет пользователя по его username.
     *
     * @param username имя пользователя
     * @throws UserNotFoundException если пользователь не найден
     */
    @Override
    public void deleteUser(String username) {
        Optional<Users> user = usersRepository.findByUsername(username);
        if(user.isPresent()) {
            usersRepository.delete(user.get());
        }
        else{
            throw new UserNotFoundException("User not found!");
        }
    }

    /**
     * Выполняет поиск пользователя по имени пользователя.
     *
     * @param username имя пользователя
     * @return {@code Optional} с найденным пользователем или пустой, если пользователь не найден
     */
    @Override
    public Optional<Users> findUserByUsername(String username) {
        return usersRepository.findByUsername(username);
    }

    /**
     * Выполняет поиск пользователя по email.
     *
     * @param email адрес электронной почты
     * @return {@code Optional} с найденным пользователем или пустой, если пользователь не найден
     */
    @Override
    public Optional<Users> findUserByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
}
