package dev.gabrielsales.bankcore.controller;

import dev.gabrielsales.bankcore.domain.entity.User;
import dev.gabrielsales.bankcore.dto.CreateUserRequest;
import dev.gabrielsales.bankcore.dto.UpdateUserRequest;
import dev.gabrielsales.bankcore.dto.UserResponse;
import dev.gabrielsales.bankcore.exception.EmailAlreadyExistsException;
import dev.gabrielsales.bankcore.exception.GlobalExceptionHandler;
import dev.gabrielsales.bankcore.exception.UserNotFoundException;
import dev.gabrielsales.bankcore.service.UserService;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateUserAndReturn201() throws Exception {
        User user = new User("Jane Doe", "jane@example.com", "secret456");
        when(userService.createUser("Jane Doe", "jane@example.com", "secret456"))
                .thenReturn(user);

        CreateUserRequest request = new CreateUserRequest("Jane Doe", "jane@example.com", "secret456");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(userService.createUser("Jane Doe", "jane@example.com", "secret456"))
                .thenThrow(new EmailAlreadyExistsException("jane@example.com"));

        CreateUserRequest request = new CreateUserRequest("Jane Doe", "jane@example.com", "secret456");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists: jane@example.com"));
    }

    @Test
    void shouldReturn200WhenUserFoundByEmail() throws Exception {
        User user = new User("Jane Doe", "jane@example.com", "secret456");
        UserResponse response = UserResponse.from(user);
        when(userService.getUserByEmail("jane@example.com")).thenReturn(response);

        mockMvc.perform(get("/users/email/{email}", "jane@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturn404WhenEmailNotFound() throws Exception {
        when(userService.getUserByEmail("missing@example.com"))
                .thenThrow(new UserNotFoundException("missing@example.com"));

        mockMvc.perform(get("/users/email/{email}", "missing@example.com"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found: missing@example.com"));
    }

    @Test
    void shouldReturn200WhenUpdateSuccessful() throws Exception {
        UUID userId = UUID.randomUUID();
        User user = new User("Jane Updated", "jane@example.com", "secret456");
        UserResponse response = UserResponse.from(user);
        when(userService.updateUser(userId, new UpdateUserRequest("Jane Updated", "jane@example.com")))
                .thenReturn(response);

        UpdateUserRequest request = new UpdateUserRequest("Jane Updated", "jane@example.com");

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Updated"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void shouldReturn404WhenUserNotFoundForUpdate() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.updateUser(userId, new UpdateUserRequest("John", "john@example.com")))
                .thenThrow(new UserNotFoundException(userId.toString()));

        UpdateUserRequest request = new UpdateUserRequest("John", "john@example.com");

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found: " + userId));
    }

    @Test
    void shouldReturn409WhenEmailAlreadyTakenForUpdate() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userService.updateUser(userId, new UpdateUserRequest("John", "taken@example.com")))
                .thenThrow(new EmailAlreadyExistsException("taken@example.com"));

        UpdateUserRequest request = new UpdateUserRequest("John", "taken@example.com");

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email already exists: taken@example.com"));
    }
}
