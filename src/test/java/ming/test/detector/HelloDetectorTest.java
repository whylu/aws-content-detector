package ming.test.detector;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class HelloDetectorTest {

    @Test
    void foundSuspicion() {
        HelloDetector detector = new HelloDetector();
        assertThat(detector.foundSuspicion(" sdfasdf sdfhell das ")).isFalse();
        assertThat(detector.foundSuspicion(" sdfasdf sdfhello das ")).isTrue();
    }
}