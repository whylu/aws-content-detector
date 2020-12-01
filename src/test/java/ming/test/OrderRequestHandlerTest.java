package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderRequestHandlerTest {

    @Test
    void handleRequest_dataInserted() {
        // using h2 in memory db
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/init.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        Map<String,Object> event = new HashMap();
        event.put("content", "this is my test content");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = handler.handleRequest(event, context);
        assertTrue(result>0);
    }

    @Test
    void handleRequest_noConnection() {
        Map<String,Object> event = new HashMap();
        event.put("content", "this is my test content");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler();
        int result = handler.handleRequest(event, context);
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }

    @Test
    void handleRequest_noEnv() {
        Map<String,Object> event = new HashMap();
        event.put("content", "this is my test content");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(new Environment());
        int result = handler.handleRequest(event, context);
        assertEquals(result, ErrorCode.DB_CONNECT_FAILED.getCode());
    }

    @Test
    void handleRequest_connectFailed() {
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("wrong url");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        Map<String,Object> event = new HashMap();
        event.put("content", "this is my test content");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = handler.handleRequest(event, context);
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

        Map<String,Object> event = new HashMap();

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = handler.handleRequest(event, context);
        assertEquals(result, ErrorCode.MISSING_CONTENT.getCode());
    }


    @Test
    void handleRequest_insertFailed() {
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/drop_all_objects.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'"); // drop table
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");
        Map<String,Object> event = new HashMap();
        event.put("content", "this is my test content");

        Context context = new TestContext();
        OrderRequestHandler handler = new OrderRequestHandler(env);
        int result = handler.handleRequest(event, context);
        assertEquals(result, ErrorCode.DB_INSERT_FAILED.getCode());
    }
}