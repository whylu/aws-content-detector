package ming.test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OrderRequestHandler extends ProxyHandler {
    private static Logger logger = LoggerFactory.getLogger(OrderRequestHandler.class);
    private static final String SQL_INSERT = "INSERT INTO submit_order (content, submit_time) VALUES (?, extract(epoch from now() at time zone 'UTC')::int)";
    private Environment env;
    private Connection connection;

    public OrderRequestHandler() {
        this.connection = createDBConnection();
    }
    public OrderRequestHandler(Environment env) {
        this.env = env;
        this.connection = createDBConnection();
    }

    private Connection createDBConnection() {
        if(env==null) {
            env = new Environment();
        }
        String dbUrl = env.getDbUrl();
        String username = env.getDbUsername();
        String password = env.getDbPassword();
        if(dbUrl==null || username==null || password==null) {
            return null;
        }
        try {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            return conn;
        } catch (Exception ex) {
            return null;
        }
    }


    @Override
    protected String handleBody(String body, Context context) {

        if(connection==null) {
            logger.warn("there is no connection, no more operation");
            return ErrorCode.DB_CONNECT_FAILED.getCodeStr();
        }
        if(body==null || body.isEmpty()) {
            logger.warn("MISSING_CONTENT");
            return ErrorCode.MISSING_CONTENT.getCodeStr();
        }

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, body);
            int row = preparedStatement.executeUpdate();
            if(row==1) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                generatedKeys.next();
                int rowId = generatedKeys.getInt(1);
                return String.valueOf(rowId);
            }
        } catch (Exception ex) {
            logger.warn("failed to insert db" + ex.getMessage());
            ex.printStackTrace();
        }
        return ErrorCode.DB_INSERT_FAILED.getCodeStr(); // failed
    }

    private static Map responseHeader = createResponseHeader();


    private static Map createResponseHeader() {
        Map responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", "text/plain");
        return Collections.unmodifiableMap(responseHeader);
    }

}
