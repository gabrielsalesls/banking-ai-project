package dev.gabrielsales.bankcore.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailAlreadyExistsExceptionTest {

    @Test
    void shouldContainEmailInMessage() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("test@example.com");

        assertThat(exception.getMessage()).contains("test@example.com");
    }

    @Test
    void shouldBeRuntimeException() {
        EmailAlreadyExistsException exception = new EmailAlreadyExistsException("test@example.com");

        assertThat(exception).isInstanceOf(RuntimeException.class);
    }
}
