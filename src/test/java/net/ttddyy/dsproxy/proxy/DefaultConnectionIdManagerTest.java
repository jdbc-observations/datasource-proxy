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

        assertThat(idManager.getId(null)).isEqualTo("1");
        assertThat(idManager.getId(null)).isEqualTo("2");
        assertThat(idManager.getId(null)).isEqualTo("3");

        Connection conn = mock(Connection.class);
        idManager = new DefaultConnectionIdManager();

        assertThat(idManager.getId(conn)).isEqualTo("1");
        assertThat(idManager.getId(conn)).isEqualTo("2");
        assertThat(idManager.getId(conn)).isEqualTo("3");
    }

    @Test
    public void getOpenConnectionIds() {
        DefaultConnectionIdManager idManager = new DefaultConnectionIdManager();

        assertThat(idManager.getOpenConnectionIds()).isEmpty();

        String id1 = idManager.getId(null);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1);

        String id2 = idManager.getId(null);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1, id2);

        idManager.addClosedId(id2);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1);

        idManager.addClosedId(id1);
        assertThat(idManager.getOpenConnectionIds()).isEmpty();

    }
}
