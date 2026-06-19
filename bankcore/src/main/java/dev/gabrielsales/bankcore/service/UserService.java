package dev.gabrielsales.bankcore.service;

import dev.gabrielsales.bankcore.domain.entity.User;
import dev.gabrielsales.bankcore.dto.UpdateUserRequest;
import dev.gabrielsales.bankcore.dto.UserResponse;
import dev.gabrielsales.bankcore.exception.EmailAlreadyExistsException;
import dev.gabrielsales.bankcore.exception.UserNotFoundException;
import dev.gabrielsales.bankcore.repository.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }

        User user = new User(name, email, password);
        return userRepository.save(user);
    }

    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserResponse::from)
                .orElseThrow(() -> new UserNotFoundException(email));
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id.toString()));

        if (!user.getEmail().equals(request.email()) && userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        user.setName(request.name());
        user.setEmail(request.email());

        return UserResponse.from(userRepository.save(user));
    }
}
