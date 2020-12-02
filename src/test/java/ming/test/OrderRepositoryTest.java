package ming.test;

import ming.test.config.Environment;
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
    void insertOrder() {
        String initSqlPath = OrderRequestHandlerTest.class.getResource("/h2/init.sql").getPath();
        Environment env = mock(Environment.class);
        when(env.getDbUrl()).thenReturn("jdbc:h2:mem:test;INIT=runscript from '"+initSqlPath+"'");
        when(env.getDbUsername()).thenReturn("username");
        when(env.getDbPassword()).thenReturn("password");

        OrderRepository orderRepository = new OrderRepository(env);
        int result = orderRepository.insertOrder("this is my content");
        assertThat(result).isGreaterThan(0);
    }
}