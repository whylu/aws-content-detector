package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderRequestHandler extends ProxyHandler {
    private static Logger logger = LoggerFactory.getLogger(OrderRequestHandler.class);
    private OrderRepository orderRepository;

    public OrderRequestHandler() {
        this(new OrderRepository(new Environment()));
    }
    public OrderRequestHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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

}
