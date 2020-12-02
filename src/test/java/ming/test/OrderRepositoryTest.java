package ming.test;

import ming.test.config.Environment;
import ming.test.model.ErrorCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OrderRepositoryTest {

    @Test
    void constructor_hasConnection() throws NoSuchFieldException, IllegalAccessException {
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        OrderRepository orderRepository = new OrderRepository(env);
        assertThat(orderRepository.isActive()).isTrue();
    }

    @Test
    void constructor_noConnectionConfig() throws NoSuchFieldException, IllegalAccessException {
        OrderRepository orderRepository = new OrderRepository(new Environment());
        assertThat(orderRepository.isActive()).isFalse();
        orderRepository = new OrderRepository(null);
        assertThat(orderRepository.isActive()).isFalse();
    }


    @Test
    void constructor_connectFailed() {
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("wrong url");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        OrderRepository orderRepository = new OrderRepository(env);
        assertThat(orderRepository.isActive()).isFalse();
    }

    @Test
    void insertOrder_success() {
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/init.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        OrderRepository orderRepository = new OrderRepository(env);
        int result = orderRepository.insertOrder("this is my content");
        assertThat(result).isGreaterThan(0);
    }

    @Test
    void insertOrder_failed() {
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/drop_submit_order.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        OrderRepository orderRepository = new OrderRepository(env);
        int result = orderRepository.insertOrder("this is my content");
        assertThat(result).isEqualTo(ErrorCode.DB_INSERT_FAILED.getCode());
    }

}