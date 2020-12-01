package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class ProxyHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent>  {

    abstract protected String handleBody(String input, Context context);

    private static Map responseHeader = createResponseHeader();
    private static Map createResponseHeader() {
        Map responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", "text/plain");
        return Collections.unmodifiableMap(responseHeader);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String responseEvent = handleBody(input.getBody(), context);
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setIsBase64Encoded(false);
        response.setBody(responseEvent);
        response.setHeaders(responseHeader);
        return response;
    }

}
