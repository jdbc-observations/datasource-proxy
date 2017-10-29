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

    @Test
    public void getOpenConnectionIds() {
        DefaultConnectionIdManager idManager = new DefaultConnectionIdManager();

        assertThat(idManager.getOpenConnectionIds()).isEmpty();

        long id1 = idManager.getId(null);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1);

        long id2 = idManager.getId(null);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1, id2);

        idManager.addClosedId(id2);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1);

        idManager.addClosedId(id1);
        assertThat(idManager.getOpenConnectionIds()).isEmpty();

    }
}
