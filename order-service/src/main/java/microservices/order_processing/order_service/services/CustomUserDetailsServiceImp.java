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

@Service
@RequiredArgsConstructor
public class CustomUserDetailsServiceImp implements UserDetailsService {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));

        return new UserDetailsImpl(user);
    }
}
