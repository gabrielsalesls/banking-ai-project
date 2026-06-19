package dev.gabrielsales.bankcore.controller;

import dev.gabrielsales.bankcore.domain.entity.User;
import dev.gabrielsales.bankcore.dto.CreateUserRequest;
import dev.gabrielsales.bankcore.exception.EmailAlreadyExistsException;
import dev.gabrielsales.bankcore.exception.GlobalExceptionHandler;
import dev.gabrielsales.bankcore.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
