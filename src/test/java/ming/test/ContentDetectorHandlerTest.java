package ming.test;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import ming.test.detector.Detector;
import ming.test.detector.HelloDetector;
import ming.test.model.ErrorCode;
import ming.test.model.SubmitOrder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class ContentDetectorHandlerTest {

    @Test
    void handleRequest_noDetector() {
        ContentDetectorHandler handler = new ContentDetectorHandler();
        String result = handler.handleRequest(createEvent("this is my content"), new TestContext());
        assertThat(result).isEqualTo(ErrorCode.DETECTOR_NOT_DEFINED.name());
    }

    @Test
    void handleRequest_noRepository() {
        ContentDetectorHandler handler = new ContentDetectorHandler();
        injectDetector(handler, new HelloDetector());
        String result = handler.handleRequest(createEvent("this is my content"), new TestContext());
        assertThat(result).isEqualTo(ErrorCode.DB_CONNECT_FAILED.name());
    }

    @Test
    void handleRequest_rowIdNotNumber() {
        ContentDetectorHandler handler = new ContentDetectorHandler();
        injectDetector(handler, new HelloDetector());
        OrderRepository repository = mock(OrderRepository.class);
        doReturn(true).when(repository).isActive();
        injectOrderRepository(handler, repository);
        String result = handler.handleRequest(createEvent("this is my content"), new TestContext());
        assertThat(result).isEqualTo(ErrorCode.ROW_ID_INVALID.name());
    }

    @Test
    void handleRequest_rowIdNegative() {
        ContentDetectorHandler handler = new ContentDetectorHandler();
        injectDetector(handler, new HelloDetector());
        OrderRepository repository = mock(OrderRepository.class);
        doReturn(true).when(repository).isActive();
        injectOrderRepository(handler, repository);
        String result = handler.handleRequest(createEvent("-1"), new TestContext());
        assertThat(result).isEqualTo(ErrorCode.ROW_ID_INVALID.name());
    }

    @Test
    void handleRequest_orderNotFound() {
        ContentDetectorHandler handler = new ContentDetectorHandler();
        injectDetector(handler, new HelloDetector());
        OrderRepository repository = mock(OrderRepository.class);
        doReturn(true).when(repository).isActive();
        doReturn(null).when(repository).getOrder(100);
        injectOrderRepository(handler, repository);
        String result = handler.handleRequest(createEvent("100"), new TestContext());
        assertThat(result).isEqualTo(ErrorCode.ORDER_NOT_FOUND.name());
    }


    @Test
    void handleRequest_detectDone() {
        OrderRepository repository = mock(OrderRepository.class);
        ContentDetectorHandler handler = new ContentDetectorHandler();
        HelloDetector detector = spy(new HelloDetector());
        injectDetector(handler, detector);
        injectOrderRepository(handler, repository);

        doReturn(true).when(repository).isActive();
        SubmitOrder submitOrder = new SubmitOrder();
        submitOrder.setId(100);
        submitOrder.setContent("this is my content");
        doReturn(submitOrder).when(repository).getOrder(100);

        handler.handleRequest(createEvent("100"), new TestContext());
        verify(detector).foundSuspicion(eq("this is my content"));
    }


    private void injectOrderRepository(ContentDetectorHandler handler, OrderRepository repository) {
        try {
            Field field = ContentDetectorHandler.class.getDeclaredField("orderRepository");
            field.setAccessible(true);
            field.set(handler, repository);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void injectDetector(ContentDetectorHandler handler, Detector detector) {
        try {
            Field field = ContentDetectorHandler.class.getDeclaredField("detector");
            field.setAccessible(true);
            field.set(handler, detector);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private SNSEvent createEvent(String message) {
        SNSEvent.SNSRecord snsRecord = new SNSEvent.SNSRecord();
        SNSEvent.SNS sns  = new SNSEvent.SNS();
        sns.setMessage(message);
        snsRecord.setSns(sns);
        SNSEvent event = new SNSEvent();
        event.setRecords(Collections.singletonList(snsRecord));
        return event;
    }
}