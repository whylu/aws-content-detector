package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import ming.test.model.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class OrderRequestHandlerTest {

    String body = "this is my test content";
    Context context = new TestContext();

    @Test
    void handleRequest_dataInserted() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        doReturn(true).when(orderRepository).isActive();
        doReturn(25).when(orderRepository).insertOrder(body);

        OrderRequestHandler handler = new OrderRequestHandler(orderRepository);
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertThat(result).isEqualTo(25);
    }

    @Test
    void handleRequest_missingContent() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        doReturn(true).when(orderRepository).isActive();

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(orderRepository);
        int result = Integer.parseInt(handler.handleBody(null, context));
        assertEquals(result, ErrorCode.MISSING_CONTENT.getCode());
    }


    @Test
    void handleRequest_repositoryInactive() {
        OrderRepository orderRepository = mock(OrderRepository.class);
        doReturn(false).when(orderRepository).isActive();

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(orderRepository);
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }

    @Test
    void handleRequest_repositoryInactive_defaultConstructor() {
        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler();
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }

//     manually test, need setting following environment variables:
//     DB_URL=<url to DB>;
//     DB_USERNAME=<DB username>;
//     DB_PASSWORD=<DB password>
//    @Test
    void manuallyTest_handleRequest() {
        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler();
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertTrue(result>0);
    }
}