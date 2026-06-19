package dev.gabrielsales.bankcore.service;

import dev.gabrielsales.bankcore.domain.entity.User;
import dev.gabrielsales.bankcore.exception.EmailAlreadyExistsException;
import dev.gabrielsales.bankcore.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = new User("John Doe", "john@example.com", "secret123");
    }

    @Test
    void shouldCreateUserWhenEmailDoesNotExist() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("John Doe", "john@example.com", "secret123");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("John Doe", "john@example.com", "secret123"))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("john@example.com");

        verify(userRepository).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnUserWithGeneratedId() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("John Doe", "john@example.com", "secret123");

        assertThat(result).isSameAs(savedUser);
    }
}
