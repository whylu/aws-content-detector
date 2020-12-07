package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import ming.test.config.Environment;
import ming.test.detector.Detector;
import ming.test.detector.HelloDetector;
import ming.test.model.ErrorCode;
import ming.test.model.SubmitOrder;
import ming.test.utils.LogUtils;

public class ContentDetectorHandler implements RequestHandler<SNSEvent, String> {
    private OrderRepository orderRepository;
    private Detector detector;
    private String strategy;

    public ContentDetectorHandler() {
        Environment env = new Environment();
        orderRepository = new OrderRepository(env);
        strategy = env.getDetectStrategy();
        detector = Detector.Factory.build(strategy);
    }

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        if(this.detector==null) {
            LogUtils.log("there is no detector, no more operation");
            return ErrorCode.DETECTOR_NOT_DEFINED.name();
        }
        if(!orderRepository.isActive()) {
            LogUtils.log("there is no connection, no more operation");
            return ErrorCode.DB_CONNECT_FAILED.name();
        }

        String message = event.getRecords().get(0).getSNS().getMessage();
        try {
            int rowId = Integer.parseInt(message);
            if(rowId<=0) {
                LogUtils.log("rowId invalid: "+ rowId);
                return ErrorCode.ROW_ID_INVALID.name();
            }

            SubmitOrder order = orderRepository.getOrder(rowId);
            if(order==null) {
                LogUtils.log("order not found: "+ rowId);
                return ErrorCode.ORDER_NOT_FOUND.name();
            }
            boolean isSuspicion = detector.foundSuspicion(order.getContent());

            int historyRowId = orderRepository.createHistory(order.getId(), strategy, isSuspicion);
            return String.valueOf(historyRowId);
        } catch (NumberFormatException ex) {
            LogUtils.log("Expecting input message is an integer, but '"+message+"'");
            return ErrorCode.ROW_ID_INVALID.name();
        }
    }

}
