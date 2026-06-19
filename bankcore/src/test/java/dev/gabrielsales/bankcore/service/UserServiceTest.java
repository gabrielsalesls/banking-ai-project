package dev.gabrielsales.bankcore.service;

import dev.gabrielsales.bankcore.domain.entity.User;
import dev.gabrielsales.bankcore.dto.UpdateUserRequest;
import dev.gabrielsales.bankcore.dto.UserResponse;
import dev.gabrielsales.bankcore.exception.EmailAlreadyExistsException;
import dev.gabrielsales.bankcore.exception.UserNotFoundException;
import dev.gabrielsales.bankcore.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
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

    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldReturnUserWithGeneratedId() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.createUser("John Doe", "john@example.com", "secret123");

        assertThat(result).isSameAs(savedUser);
    }

    @Test
    void shouldReturnUserResponseWhenEmailFound() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(savedUser));

        UserResponse result = userService.getUserByEmail("john@example.com");

        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.email()).isEqualTo("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenEmailNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail("unknown@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("unknown@example.com");

        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void shouldUpdateUserSuccessfullyWhenEmailUnchanged() {
        var request = new UpdateUserRequest("John Updated", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = userService.updateUser(userId, request);

        assertThat(result.name()).isEqualTo("John Updated");
        assertThat(result.email()).isEqualTo("john@example.com");
        verify(userRepository).findById(userId);
        verify(userRepository, never()).existsByEmail(any());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldUpdateUserSuccessfullyWhenNewEmailNotTaken() {
        var request = new UpdateUserRequest("John Doe", "newemail@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = userService.updateUser(userId, request);

        assertThat(result.name()).isEqualTo("John Doe");
        assertThat(result.email()).isEqualTo("newemail@example.com");
        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("newemail@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowUserNotFoundExceptionWhenUserNotFoundForUpdate() {
        var request = new UpdateUserRequest("John Doe", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining(userId.toString());

        verify(userRepository).findById(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowEmailAlreadyExistsExceptionWhenNewEmailTaken() {
        var request = new UpdateUserRequest("John Doe", "taken@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("taken@example.com");

        verify(userRepository).findById(userId);
        verify(userRepository).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
}
