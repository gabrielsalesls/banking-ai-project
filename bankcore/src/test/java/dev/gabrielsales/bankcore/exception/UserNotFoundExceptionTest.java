package dev.gabrielsales.bankcore.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserNotFoundExceptionTest {

    @Test
    void shouldContainEmailInMessage() {
        UserNotFoundException exception = new UserNotFoundException("test@example.com");

        assertThat(exception.getMessage()).contains("test@example.com");
    }

    @Test
    void shouldBeRuntimeException() {
        UserNotFoundException exception = new UserNotFoundException("test@example.com");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
