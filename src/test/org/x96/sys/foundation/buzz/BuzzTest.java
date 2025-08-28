package org.x96.sys.foundation.buzz;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Testes para a classe Buzz")
class BuzzTest {

    @Nested
    @DisplayName("Testes de Construção")
    class ConstructorTests {

        @Test
        @DisplayName("Deve criar Buzz com código, bee e mensagem")
        void shouldCreateBuzzWithCodeBeeAndMessage() {
            // Arrange
            int code = 404;
            String bee = "NOT_FOUND";
            String msg = "Recurso não encontrado";

            // Act
            Buzz buzz = new Buzz(code, bee, msg);

            // Assert
            assertNotNull(buzz);
            assertNotNull(buzz.getMessage());
            assertTrue(buzz.getMessage().contains("0x194")); // 404 em hex
            assertTrue(buzz.getMessage().contains(bee));
            assertTrue(buzz.getMessage().contains(msg));
        }

        @Test
        @DisplayName("Deve criar Buzz com código, bee, mensagem e causa")
        void shouldCreateBuzzWithCodeBeeMessageAndCause() {
            // Arrange
            int code = 500;
            String bee = "INTERNAL_ERROR";
            String msg = "Erro interno do servidor";
            Exception cause = new IllegalArgumentException("Argumento inválido");

            // Act
            Buzz buzz = new Buzz(code, bee, msg, cause);

            // Assert
            assertNotNull(buzz);
            assertEquals(cause, buzz.getCause());
            assertNotNull(buzz.getMessage());
        }

        @Test
        @DisplayName("Deve aceitar valores nulos para bee e mensagem")
        void shouldAcceptNullValues() {
            // Act & Assert
            assertDoesNotThrow(() -> new Buzz(0, null, null));
            assertDoesNotThrow(() -> new Buzz(100, "TEST", null));
            assertDoesNotThrow(() -> new Buzz(200, null, "Mensagem"));
        }
    }

    @Nested
    @DisplayName("Testes do Método format")
    class FormatMethodTests {

        @Test
        @DisplayName("Deve formatar mensagem corretamente com valores válidos")
        void shouldFormatMessageCorrectly() {
            // Arrange
            int code = 123;
            String bee = "TEST_BEE";
            String msg = "Mensagem de teste";

            // Act
            String result = Buzz.format(code, bee, msg);

            // Assert
            assertNotNull(result);
            assertTrue(result.contains("0x7B")); // 123 em hex
            assertTrue(result.contains("TEST_BEE"));
            assertTrue(result.contains("Mensagem de teste"));
            assertTrue(result.contains("🦕")); // BUGS[8]
            assertTrue(result.contains("🐝")); // BUGS[5]
            assertTrue(result.contains("🌵")); // BUGS[9]
        }

        @Test
        @DisplayName("Deve formatar código zero corretamente")
        void shouldFormatZeroCodeCorrectly() {
            // Act
            String result = Buzz.format(0, "ZERO", "Zero code");

            // Assert
            assertTrue(result.contains("0x0"));
        }

        @Test
        @DisplayName("Deve formatar código negativo corretamente")
        void shouldFormatNegativeCodeCorrectly() {
            // Act
            String result = Buzz.format(-1, "NEGATIVE", "Código negativo");

            // Assert
            assertTrue(result.contains("0xFFFFFFFF")); // -1 em hex (complemento de 2)
        }

        @Test
        @DisplayName("Deve lidar com valores nulos no formato")
        void shouldHandleNullValuesInFormat() {
            // Act & Assert
            assertDoesNotThrow(() -> Buzz.format(100, null, null));

            String result = Buzz.format(100, null, "test");
            assertTrue(result.contains("null"));
        }
    }

    @Nested
    @DisplayName("Testes de Herança e Comportamento")
    class InheritanceTests {

        @Test
        @DisplayName("Deve ser uma RuntimeException")
        void shouldBeRuntimeException() {
            // Arrange
            Buzz buzz = new Buzz(1, "TEST", "test message");

            // Assert
            assertTrue(buzz instanceof RuntimeException);
            assertTrue(buzz instanceof Exception);
            assertTrue(buzz instanceof Throwable);
        }

        @Test
        @DisplayName("Deve ser lançável como exceção")
        void shouldBeThrowable() {
            // Act & Assert
            assertThrows(
                    Buzz.class,
                    () -> {
                        throw new Buzz(404, "NOT_FOUND", "Recurso não encontrado");
                    });
        }

        @Test
        @DisplayName("Deve preservar stack trace quando lançada")
        void shouldPreserveStackTrace() {
            try {
                throw new Buzz(500, "ERROR", "Erro de teste");
            } catch (Buzz buzz) {
                assertNotNull(buzz.getStackTrace());
                assertTrue(buzz.getStackTrace().length > 0);
            }
        }
    }

    @Nested
    @DisplayName("Testes das Constantes")
    class ConstantsTests {

        @Test
        @DisplayName("Deve ter constantes ANSI corretas")
        void shouldHaveCorrectAnsiConstants() {
            assertEquals("\u001B[0m", Buzz.ANSI_RESET);
            assertEquals("\u001B[31m", Buzz.ANSI_RED);
            assertEquals("\u001B[32m", Buzz.ANSI_GREEN);
        }

        @Test
        @DisplayName("Deve ter array de bugs com 10 elementos")
        void shouldHaveBugsArrayWithTenElements() {
            // Usando reflexão para acessar o array privado
            try {
                var field = Buzz.class.getDeclaredField("BUGS");
                field.setAccessible(true);
                String[] bugs = (String[]) field.get(null);

                assertEquals(10, bugs.length);
                assertEquals("🐞", bugs[0]);
                assertEquals("🐝", bugs[5]);
                assertEquals("🦕", bugs[8]);
                assertEquals("🌵", bugs[9]);
            } catch (Exception e) {
                fail("Falha ao acessar array BUGS: " + e.getMessage());
            }
        }
    }

    @Nested
    @DisplayName("Testes de Casos Extremos")
    class EdgeCaseTests {

        @Test
        @DisplayName("Deve lidar com códigos muito grandes")
        void shouldHandleLargeCodes() {
            int largeCode = Integer.MAX_VALUE;
            assertDoesNotThrow(() -> new Buzz(largeCode, "LARGE", "Código grande"));
        }

        @Test
        @DisplayName("Deve lidar com strings muito longas")
        void shouldHandleLongStrings() {
            String longBee = "A".repeat(1000);
            String longMsg = "B".repeat(1000);

            assertDoesNotThrow(() -> new Buzz(1, longBee, longMsg));
        }

        @Test
        @DisplayName("Deve lidar com caracteres especiais")
        void shouldHandleSpecialCharacters() {
            String specialBee = "TEST_🚀_ÇÃO";
            String specialMsg = "Mensagem com acentos: ção, não, coração ❤️";

            Buzz buzz = new Buzz(1, specialBee, specialMsg);
            assertTrue(buzz.getMessage().contains(specialBee));
            assertTrue(buzz.getMessage().contains(specialMsg));
        }
    }

    @Nested
    @DisplayName("Testes de Integração")
    class IntegrationTests {

        @Test
        @DisplayName("Deve funcionar em cenário de try-catch completo")
        void shouldWorkInTryCatchScenario() {
            // Arrange
            int expectedCode = 403;
            String expectedBee = "FORBIDDEN";
            String expectedMsg = "Acesso negado";

            try {
                // Act
                throw new Buzz(expectedCode, expectedBee, expectedMsg);
            } catch (Buzz buzz) {
                // Assert
                String message = buzz.getMessage();
                assertTrue(message.contains("0x193")); // 403 em hex
                assertTrue(message.contains(expectedBee));
                assertTrue(message.contains(expectedMsg));
                assertNull(buzz.getCause());
            }
        }

        @Test
        @DisplayName("Deve manter causa original em cadeia de exceções")
        void shouldMaintainOriginalCauseInExceptionChain() {
            // Arrange
            Exception originalCause = new IllegalStateException("Estado inválido");
            Buzz buzz = new Buzz(500, "CHAIN", "Erro em cadeia", originalCause);

            try {
                throw new RuntimeException("Wrapper", buzz);
            } catch (RuntimeException wrapper) {
                // Assert
                assertEquals(buzz, wrapper.getCause());
                assertEquals(originalCause, wrapper.getCause().getCause());
            }
        }
    }
}
