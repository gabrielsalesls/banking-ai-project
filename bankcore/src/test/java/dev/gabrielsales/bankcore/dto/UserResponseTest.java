package dev.gabrielsales.bankcore.dto;

import dev.gabrielsales.bankcore.domain.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResponseTest {

    @Test
    void shouldMapUserToResponseExcludingPassword() {
        User user = new User("Alice", "alice@example.com", "pwd123");

        UserResponse response = UserResponse.from(user);

        assertThat(response.name()).isEqualTo("Alice");
        assertThat(response.email()).isEqualTo("alice@example.com");
        assertThat(response.id()).isNull();
        assertThat(response.createdAt()).isNull();
        assertThat(response.updatedAt()).isNull();
    }

    @Test
    void shouldNotExposePasswordField() {
        User user = new User("Bob", "bob@example.com", "superSecret");

        UserResponse response = UserResponse.from(user);

        assertThat(response.getClass().getRecordComponents())
                .noneMatch(component -> component.getName().equals("password"));
    }
}
