package [# th:text="${java_package}"/];

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class HelloIT {

    private final HelloSystem helloSystem = new HelloSystem();

    @Test
    void testHelloEndpoint() {
        assertThat(helloSystem.hello()).isEqualTo("Hello World");
    }
}
