package ming.test;

import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class OrderRepository {
    private static Logger logger = LoggerFactory.getLogger(OrderRepository.class);

    private Connection connection;
    private static final String SQL_INSERT = "INSERT INTO submit_order (content, submit_time) VALUES (?, extract(epoch from now() at time zone 'UTC')::int)";

    public OrderRepository(Environment env) {
        connection = createDBConnection(env);
    }

    private Connection createDBConnection(Environment env) {
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

    public boolean isActive() {
        return connection!=null;
    }


    public int insertOrder(String body) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, body);
            int row = preparedStatement.executeUpdate();
            if(row==1) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                generatedKeys.next();
                int rowId = generatedKeys.getInt(1);
                return rowId;
            }
        } catch (Exception ex) {
            logger.warn("failed to insert db", ex);
            ex.printStackTrace();
        }
        return ErrorCode.DB_INSERT_FAILED.getCode();
    }

}
