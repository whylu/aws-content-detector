package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderRequestHandlerTest {

    String body = "this is my test content";

    @Test
    void handleRequest_dataInserted() {
        // using h2 in memory db
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/init.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertTrue(result>0);
    }

    @Test
    void handleRequest_noConnection() {
        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler();
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }

    @Test
    void handleRequest_noEnv() {
        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(new Environment());
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }

    @Test
    void handleRequest_connectFailed() {
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("wrong url");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }


    @Test
    void handleRequest_missingContent() {
        // using h2 in memory db
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/init.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = Integer.parseInt(handler.handleBody(null, context));
        assertEquals(result, ErrorCode.MISSING_CONTENT.getCode());
    }


    @Test
    void handleRequest_insertFailed() {
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/drop_submit_order.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'"); // drop table
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = Integer.parseInt(handler.handleBody(body, context));
        assertEquals(result, ErrorCode.DB_INSERT_FAILED.getCode());
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