package ming.test.detector;

public class HelloDetector implements Detector {
    @Override
    public boolean foundSuspicion(String content) {
        return content.contains("hello");
    }
}
