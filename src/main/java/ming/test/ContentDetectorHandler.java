package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import ming.test.config.Environment;
import ming.test.detector.Detector;
import ming.test.model.ErrorCode;
import ming.test.model.SubmitOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentDetectorHandler implements RequestHandler<SNSEvent, String> {
    private static Logger logger = LoggerFactory.getLogger(ContentDetectorHandler.class);
    private OrderRepository orderRepository;
    private Detector detector;

    public ContentDetectorHandler() {
        Environment env = new Environment();
        orderRepository = new OrderRepository(env);
        detector = Detector.Factory.build(env.getDetectStrategy());
    }

    @Override
    public String handleRequest(SNSEvent event, Context context) {
        if(this.detector==null) {
            logger.warn("there is no detector, no more operation");
            return ErrorCode.DETECTOR_NOT_DEFINED.name();
        }
        if(!orderRepository.isActive()) {
            logger.warn("there is no connection, no more operation");
            return ErrorCode.DB_CONNECT_FAILED.name();
        }

        String message = event.getRecords().get(0).getSNS().getMessage();
        try {
            int rowId = Integer.parseInt(message);
            if(rowId<=0) {
                logger.warn("rowId invalid: {}", rowId);
                return ErrorCode.ROW_ID_INVALID.name();
            }

            SubmitOrder order = orderRepository.getOrder(rowId);
            if(order==null) {
                logger.warn("order not found: {}", rowId);
                return ErrorCode.ORDER_NOT_FOUND.name();
            }
            boolean isSuspicion = detector.foundSuspicion(order.getContent());
            return String.valueOf(isSuspicion);
        } catch (NumberFormatException ex) {
            logger.warn("Expecting input message is an integer, but '{}'", message);
            return ErrorCode.ROW_ID_INVALID.name();
        }
    }

}
