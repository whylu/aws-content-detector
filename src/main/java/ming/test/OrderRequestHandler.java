package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderRequestHandler extends ProxyHandler {
    private static Logger logger = LoggerFactory.getLogger(OrderRequestHandler.class);
    private static final String SQL_INSERT = "INSERT INTO submit_order (content, submit_time) VALUES (?, extract(epoch from now() at time zone 'UTC')::int)";
    private OrderRepository orderRepository;

    public OrderRequestHandler() {
        this(new Environment());
    }
    public OrderRequestHandler(Environment env) {
        this.orderRepository = new OrderRepository(env);
    }


    @Override
    protected String handleBody(String body, Context context) {

        if(!orderRepository.isActive()) {
            logger.warn("there is no connection, no more operation");
            return ErrorCode.DB_CONNECT_FAILED.getCodeStr();
        }
        if(body==null || body.isEmpty()) {
            logger.warn("MISSING_CONTENT");
            return ErrorCode.MISSING_CONTENT.getCodeStr();
        }

        int rowId = orderRepository.insertOrder(body);
        return String.valueOf(rowId);
    }

    private static Map responseHeader = createResponseHeader();


    private static Map createResponseHeader() {
        Map responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", "text/plain");
        return Collections.unmodifiableMap(responseHeader);
    }

}
