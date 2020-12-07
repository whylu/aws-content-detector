package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import ming.test.utils.LogUtils;

public class OrderRequestHandler extends ProxyHandler {
    private OrderRepository orderRepository;

    public OrderRequestHandler() {
        this(new OrderRepository(new Environment()));
    }
    public OrderRequestHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Override
    protected String handleBody(String body, Context context) {
        LogUtils.set(context.getLogger());

        if(!orderRepository.isActive()) {
            LogUtils.log("there is no connection, no more operation");
            return ErrorCode.DB_CONNECT_FAILED.getCodeStr();
        }
        if(body==null || body.isEmpty()) {
            LogUtils.log("MISSING_CONTENT");
            return ErrorCode.MISSING_CONTENT.getCodeStr();
        }

        int rowId = orderRepository.insertOrder(body);
        return String.valueOf(rowId);
    }

}
