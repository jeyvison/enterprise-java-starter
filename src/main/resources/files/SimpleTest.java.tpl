package [# th:text="${java_package}"/];

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class SimpleTest {

    private HelloController testObject;

    @BeforeEach
    void setUp() {
        testObject = new HelloController();
    }

    @Test
    void test() {
        // serves as an example, should verify business logic components instead
        assertThat(testObject.hello()).startsWith("Hello");
    }
}
