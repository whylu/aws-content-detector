package ming.test;

import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import ming.test.model.SubmitOrder;
import ming.test.utils.LogUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OrderRepository {

    private Connection connection;
    private static final String SQL_INSERT = "INSERT INTO submit_order (content, submit_time) VALUES (?, extract(epoch from now() at time zone 'UTC')::int)";
    private static final String HISTORY_SQL =
            "INSERT INTO public.detect_history (order_id,strategy,found_suspicion,process_time) VALUES (?,?,?,extract(epoch from now() at time zone 'UTC')::int)";

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
            LogUtils.log("connect failed, url:"+dbUrl+", username:"+username+", password:"+password);
            return null;
        }
        try {
            Connection conn = DriverManager.getConnection(dbUrl, username, password);
            return conn;
        } catch (Exception ex) {
            LogUtils.log("connect failed, url:"+dbUrl+", username:"+username+", password:"+password+", ex:"+ex.getMessage());
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
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            int rowId = generatedKeys.getInt(1);
            return rowId;
        } catch (Exception ex) {
            LogUtils.log("failed to insert db, ex:"+ex.getMessage());
        }
        return ErrorCode.DB_INSERT_FAILED.getCode();
    }

    public SubmitOrder getOrder(int rowId) {
        String sql = "select id, content, submit_time, is_danger from submit_order where id = " + rowId;
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            boolean hasNext = resultSet.next();
            if(hasNext) {
                SubmitOrder order = new SubmitOrder();
                order.setId(resultSet.getInt(1));
                order.setContent(resultSet.getString(2));
                order.setSubmitTime(resultSet.getLong(3));
                order.setDanger(resultSet.getBoolean(4));
                return order;
            } else {
                return null;
            }
        } catch (SQLException throwables) {
            LogUtils.log("select failed, ex:"+ throwables.getMessage());
            return null;
        }
    }


    public int createHistory(int orderId, String strategy, boolean isSuspicion) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(HISTORY_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, orderId);
            preparedStatement.setString(2, strategy);
            preparedStatement.setBoolean(3, isSuspicion);

            int row = preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            generatedKeys.next();
            int rowId = generatedKeys.getInt(1);
            return rowId;
        } catch (Exception ex) {
            LogUtils.log("failed to insert db, ex:" +ex.getMessage());
        }
        return ErrorCode.DB_INSERT_FAILED.getCode();


    }
}
