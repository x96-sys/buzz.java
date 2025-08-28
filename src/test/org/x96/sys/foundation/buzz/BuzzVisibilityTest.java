package org.x96.sys.foundation.buzz;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@DisplayName("Testes de Visibilidade e Acessibilidade da Classe Buzz")
class BuzzVisibilityTest {

    @Nested
    @DisplayName("Testes de Visibilidade dos Construtores")
    class ConstructorVisibilityTests {

        @Test
        @DisplayName("Construtor com 4 parâmetros deve ser público")
        void constructorWithFourParametersShouldBePublic() throws Exception {
            // Act
            Constructor<Buzz> constructor =
                    Buzz.class.getConstructor(
                            int.class, String.class, String.class, Throwable.class);

            // Assert
            assertTrue(
                    Modifier.isPublic(constructor.getModifiers()),
                    "O construtor Buzz(int, String, String, Throwable) deve ser público");
            assertFalse(
                    Modifier.isPrivate(constructor.getModifiers()),
                    "O construtor não deve ser privado");
            assertFalse(
                    Modifier.isProtected(constructor.getModifiers()),
                    "O construtor não deve ser protegido");
        }

        @Test
        @DisplayName("Construtor com 3 parâmetros deve ser público")
        void constructorWithThreeParametersShouldBePublic() throws Exception {
            // Act
            Constructor<Buzz> constructor =
                    Buzz.class.getConstructor(int.class, String.class, String.class);

            // Assert
            assertTrue(
                    Modifier.isPublic(constructor.getModifiers()),
                    "O construtor Buzz(int, String, String) deve ser público");
            assertFalse(
                    Modifier.isPrivate(constructor.getModifiers()),
                    "O construtor não deve ser privado");
            assertFalse(
                    Modifier.isProtected(constructor.getModifiers()),
                    "O construtor não deve ser protegido");
        }

        @Test
        @DisplayName("Deve ter exatamente 2 construtores públicos")
        void shouldHaveExactlyTwoPublicConstructors() {
            // Act
            Constructor<?>[] constructors = Buzz.class.getDeclaredConstructors();
            long publicConstructors =
                    java.util.Arrays.stream(constructors)
                            .mapToInt(Constructor::getModifiers)
                            .filter(Modifier::isPublic)
                            .count();

            // Assert
            assertEquals(2, constructors.length, "Deve ter exatamente 2 construtores");
            assertEquals(2, publicConstructors, "Ambos construtores devem ser públicos");
        }
    }

    @Nested
    @DisplayName("Testes de Visibilidade dos Métodos")
    class MethodVisibilityTests {

        @Test
        @DisplayName("Método format deve ser público e estático")
        void formatMethodShouldBePublicAndStatic() throws Exception {
            // Act
            Method formatMethod =
                    Buzz.class.getMethod("format", int.class, String.class, String.class);

            // Assert
            assertTrue(
                    Modifier.isPublic(formatMethod.getModifiers()),
                    "O método format deve ser público");
            assertTrue(
                    Modifier.isStatic(formatMethod.getModifiers()),
                    "O método format deve ser estático");
            assertFalse(
                    Modifier.isPrivate(formatMethod.getModifiers()),
                    "O método format não deve ser privado");
            assertFalse(
                    Modifier.isProtected(formatMethod.getModifiers()),
                    "O método format não deve ser protegido");
        }

        @Test
        @DisplayName("Método format deve ser acessível de outra classe")
        void formatMethodShouldBeAccessibleFromAnotherClass() {
            // Act & Assert - Se conseguir chamar sem exceção, é acessível
            assertDoesNotThrow(
                    () -> {
                        String result = Buzz.format(100, "TEST", "Teste de acessibilidade");
                        assertNotNull(result);
                    },
                    "O método format deve ser acessível de outras classes");
        }
    }

    @Nested
    @DisplayName("Testes de Visibilidade dos Campos/Constantes")
    class FieldVisibilityTests {

        @Test
        @DisplayName("Constante ANSI_RESET deve ser pública, estática e final")
        void ansiResetShouldBePublicStaticFinal() throws Exception {
            // Act
            Field field = Buzz.class.getField("ANSI_RESET");

            // Assert
            assertTrue(Modifier.isPublic(field.getModifiers()), "ANSI_RESET deve ser público");
            assertTrue(Modifier.isStatic(field.getModifiers()), "ANSI_RESET deve ser estático");
            assertTrue(Modifier.isFinal(field.getModifiers()), "ANSI_RESET deve ser final");
            assertFalse(
                    Modifier.isPrivate(field.getModifiers()), "ANSI_RESET não deve ser privado");
        }

        @Test
        @DisplayName("Constante ANSI_RED deve ser pública, estática e final")
        void ansiRedShouldBePublicStaticFinal() throws Exception {
            // Act
            Field field = Buzz.class.getField("ANSI_RED");

            // Assert
            assertTrue(Modifier.isPublic(field.getModifiers()), "ANSI_RED deve ser público");
            assertTrue(Modifier.isStatic(field.getModifiers()), "ANSI_RED deve ser estático");
            assertTrue(Modifier.isFinal(field.getModifiers()), "ANSI_RED deve ser final");
        }

        @Test
        @DisplayName("Constante ANSI_GREEN deve ser pública, estática e final")
        void ansiGreenShouldBePublicStaticFinal() throws Exception {
            // Act
            Field field = Buzz.class.getField("ANSI_GREEN");

            // Assert
            assertTrue(Modifier.isPublic(field.getModifiers()), "ANSI_GREEN deve ser público");
            assertTrue(Modifier.isStatic(field.getModifiers()), "ANSI_GREEN deve ser estático");
            assertTrue(Modifier.isFinal(field.getModifiers()), "ANSI_GREEN deve ser final");
        }

        @Test
        @DisplayName("Array BUGS deve ser privado, estático e final")
        void bugsArrayShouldBePrivateStaticFinal() throws Exception {
            // Act
            Field field = Buzz.class.getDeclaredField("BUGS");

            // Assert
            assertTrue(Modifier.isPrivate(field.getModifiers()), "BUGS deve ser privado");
            assertTrue(Modifier.isStatic(field.getModifiers()), "BUGS deve ser estático");
            assertTrue(Modifier.isFinal(field.getModifiers()), "BUGS deve ser final");
            assertFalse(Modifier.isPublic(field.getModifiers()), "BUGS não deve ser público");
        }

        @Test
        @DisplayName("Array BUGS não deve ser acessível diretamente de outras classes")
        void bugsArrayShouldNotBeDirectlyAccessible() {
            // Act & Assert
            assertThrows(
                    NoSuchFieldException.class,
                    () -> {
                        Buzz.class.getField("BUGS"); // Deve falhar porque é privado
                    },
                    "Campo BUGS não deve ser acessível como campo público");
        }

        @Test
        @DisplayName("Deve ter exatamente 4 campos declarados")
        void shouldHaveExactlyFourDeclaredFields() {
            // Act
            Field[] fields = Buzz.class.getDeclaredFields();

            // Assert
            assertEquals(4, fields.length, "Deve ter exatamente 4 campos declarados");
        }

        @Test
        @DisplayName("Deve ter exatamente 3 campos públicos")
        void shouldHaveExactlyThreePublicFields() {
            // Act
            Field[] publicFields = Buzz.class.getFields();

            // Assert
            assertEquals(3, publicFields.length, "Deve ter exatamente 3 campos públicos");
        }
    }

    @Nested
    @DisplayName("Testes de Acessibilidade das Constantes Públicas")
    class PublicConstantAccessibilityTests {

        @Test
        @DisplayName("ANSI_RESET deve ser acessível de outras classes")
        void ansiResetShouldBeAccessibleFromOtherClasses() {
            // Act & Assert
            assertDoesNotThrow(
                    () -> {
                        String value = Buzz.ANSI_RESET;
                        assertEquals("\u001B[0m", value);
                    },
                    "ANSI_RESET deve ser acessível de outras classes");
        }

        @Test
        @DisplayName("ANSI_RED deve ser acessível de outras classes")
        void ansiRedShouldBeAccessibleFromOtherClasses() {
            // Act & Assert
            assertDoesNotThrow(
                    () -> {
                        String value = Buzz.ANSI_RED;
                        assertEquals("\u001B[31m", value);
                    },
                    "ANSI_RED deve ser acessível de outras classes");
        }

        @Test
        @DisplayName("ANSI_GREEN deve ser acessível de outras classes")
        void ansiGreenShouldBeAccessibleFromOtherClasses() {
            // Act & Assert
            assertDoesNotThrow(
                    () -> {
                        String value = Buzz.ANSI_GREEN;
                        assertEquals("\u001B[32m", value);
                    },
                    "ANSI_GREEN deve ser acessível de outras classes");
        }

        @Test
        @DisplayName("Constantes públicas devem ser imutáveis")
        void publicConstantsShouldBeImmutable() throws Exception {
            // Act
            Field ansiReset = Buzz.class.getField("ANSI_RESET");
            Field ansiRed = Buzz.class.getField("ANSI_RED");
            Field ansiGreen = Buzz.class.getField("ANSI_GREEN");

            // Assert
            assertTrue(
                    Modifier.isFinal(ansiReset.getModifiers()),
                    "ANSI_RESET deve ser final (imutável)");
            assertTrue(
                    Modifier.isFinal(ansiRed.getModifiers()), "ANSI_RED deve ser final (imutável)");
            assertTrue(
                    Modifier.isFinal(ansiGreen.getModifiers()),
                    "ANSI_GREEN deve ser final (imutável)");
        }
    }

    @Nested
    @DisplayName("Testes de Visibilidade da Classe")
    class ClassVisibilityTests {

        @Test
        @DisplayName("Classe Buzz deve ser pública")
        void buzzClassShouldBePublic() {
            // Assert
            assertTrue(
                    Modifier.isPublic(Buzz.class.getModifiers()), "A classe Buzz deve ser pública");
            assertFalse(
                    Modifier.isPrivate(Buzz.class.getModifiers()),
                    "A classe Buzz não deve ser privada");
            assertFalse(
                    Modifier.isProtected(Buzz.class.getModifiers()),
                    "A classe Buzz não deve ser protegida");
        }

        @Test
        @DisplayName("Classe Buzz não deve ser final nem abstract")
        void buzzClassShouldNotBeFinalNorAbstract() {
            // Assert
            assertFalse(
                    Modifier.isFinal(Buzz.class.getModifiers()),
                    "A classe Buzz não deve ser final");
            assertFalse(
                    Modifier.isAbstract(Buzz.class.getModifiers()),
                    "A classe Buzz não deve ser abstract");
        }

        @Test
        @DisplayName("Classe Buzz deve estender RuntimeException")
        void buzzClassShouldExtendRuntimeException() {
            // Assert
            assertEquals(
                    RuntimeException.class,
                    Buzz.class.getSuperclass(),
                    "A classe Buzz deve estender RuntimeException");
        }

        @Test
        @DisplayName("Classe Buzz deve ser instanciável de outras classes")
        void buzzClassShouldBeInstantiableFromOtherClasses() {
            // Act & Assert
            assertDoesNotThrow(
                    () -> {
                        Buzz buzz = new Buzz(200, "TEST", "Teste de instanciação");
                        assertNotNull(buzz);
                        assertTrue(buzz instanceof Buzz);
                    },
                    "A classe Buzz deve ser instanciável de outras classes");
        }
    }

    @Nested
    @DisplayName("Testes de Compatibilidade de Herança")
    class InheritanceCompatibilityTests {

        @Test
        @DisplayName("Métodos herdados de RuntimeException devem estar acessíveis")
        void inheritedMethodsFromRuntimeExceptionShouldBeAccessible() {
            // Arrange
            Buzz buzz = new Buzz(500, "INHERITANCE_TEST", "Teste de herança");

            // Act & Assert
            assertDoesNotThrow(
                    () -> {
                        String message = buzz.getMessage();
                        Throwable cause = buzz.getCause();
                        StackTraceElement[] stackTrace = buzz.getStackTrace();
                        String toString = buzz.toString();

                        // Verificar que os métodos são acessíveis
                        assertNotNull(message);
                        // cause pode ser null, mas o método deve ser acessível
                        assertNotNull(stackTrace);
                        assertNotNull(toString);
                    },
                    "Métodos herdados devem estar acessíveis");
        }

        @Test
        @DisplayName("Buzz deve poder ser usado polimorficamente como RuntimeException")
        void buzzShouldBeUsablePolymorphicallyAsRuntimeException() {
            // Act & Assert
            assertDoesNotThrow(
                    () -> {
                        RuntimeException runtimeException =
                                new Buzz(400, "POLYMORPHISM", "Teste polimórfico");

                        // Deve poder usar métodos da interface RuntimeException
                        String message = runtimeException.getMessage();
                        assertNotNull(message);

                        // Deve poder fazer cast de volta
                        assertTrue(runtimeException instanceof Buzz);
                        Buzz castedBuzz = (Buzz) runtimeException;
                        assertNotNull(castedBuzz);
                    },
                    "Buzz deve ser usável polimorficamente");
        }
    }

    @Nested
    @DisplayName("Testes de Segurança e Encapsulamento")
    class SecurityAndEncapsulationTests {

        @Test
        @DisplayName("Array BUGS não deve ser modificável através de reflexão sem setAccessible")
        void bugsArrayShouldNotBeModifiableWithoutSetAccessible() throws Exception {
            // Act
            Field bugsField = Buzz.class.getDeclaredField("BUGS");

            // Assert
            assertThrows(
                    IllegalAccessException.class,
                    () -> {
                        bugsField.get(null); // Deve falhar sem setAccessible(true)
                    },
                    "Campo privado BUGS não deve ser acessível sem setAccessible(true)");
        }

        @Test
        @DisplayName("Interface pública deve expor apenas o necessário")
        void publicInterfaceShouldExposeOnlyWhatIsNecessary() {
            // Act
            Method[] publicMethods = Buzz.class.getMethods();
            Field[] publicFields = Buzz.class.getFields();

            // Contar métodos próprios (excluindo herdados)
            Method[] declaredMethods = Buzz.class.getDeclaredMethods();
            long ownPublicMethods =
                    java.util.Arrays.stream(declaredMethods)
                            .mapToInt(Method::getModifiers)
                            .filter(Modifier::isPublic)
                            .count();

            // Assert
            assertEquals(
                    3,
                    publicFields.length,
                    "Deve ter exatamente 3 campos públicos (constantes ANSI)");
            assertEquals(
                    1, ownPublicMethods, "Deve ter exatamente 1 método público próprio (format)");

            // Verificar que métodos herdados também estão disponíveis
            assertTrue(publicMethods.length > 1, "Deve ter métodos herdados acessíveis");
        }
    }
}
