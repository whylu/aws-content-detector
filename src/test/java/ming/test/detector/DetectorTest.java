package ming.test.detector;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DetectorTest {

    @Test
    void newInstance_success() {
        Detector detector = Detector.Factory.build("ming.test.detector.HelloDetector");
        assertThat(detector).isInstanceOf(HelloDetector.class);
    }

    @Test
    void newInstance_classNotFound() {
        Detector detector = Detector.Factory.build("ming.test.detector.NoSuchDetector");
        assertThat(detector).isNull();
    }
}