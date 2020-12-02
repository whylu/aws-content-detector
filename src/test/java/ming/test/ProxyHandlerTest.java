package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProxyHandlerTest {

    @Test
    void handleBody() {
        APIGatewayProxyRequestEvent event = new APIGatewayProxyRequestEvent();
        event.setBody("any body");

        MyProxyHandler myProxyHandler = new MyProxyHandler();
        APIGatewayProxyResponseEvent responseEvent = myProxyHandler.handleRequest(event, new TestContext());
        assertEquals("hello", responseEvent.getBody());
    }

    class MyProxyHandler extends ProxyHandler {

        @Override
        protected String handleBody(String input, Context context) {
            return "hello";
        }
    }
}