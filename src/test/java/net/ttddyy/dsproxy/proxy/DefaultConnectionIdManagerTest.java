package net.ttddyy.dsproxy.proxy;

import org.junit.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4.2
 */
public class DefaultConnectionIdManagerTest {

    @Test
    public void getId() {
        DefaultConnectionIdManager idManager = new DefaultConnectionIdManager();

        assertThat(idManager.getId(null)).isEqualTo(1L);
        assertThat(idManager.getId(null)).isEqualTo(2L);
        assertThat(idManager.getId(null)).isEqualTo(3L);

        Connection conn = mock(Connection.class);
        idManager = new DefaultConnectionIdManager();

        assertThat(idManager.getId(conn)).isEqualTo(1L);
        assertThat(idManager.getId(conn)).isEqualTo(2L);
        assertThat(idManager.getId(conn)).isEqualTo(3L);
    }

}
