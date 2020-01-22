package net.ttddyy.dsproxy.proxy;

import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Tests for {@link GlobalConnectionIdManager}.
 *
 * @author Tadaya Tsuyukubo
 */
public class GlobalConnectionIdManagerTest {

    @Before
    public void setUp() {
        GlobalConnectionIdManager.resetId();
    }

    @Test
    public void getId() {
        GlobalConnectionIdManager idManager1 = new GlobalConnectionIdManager();
        GlobalConnectionIdManager idManager2 = new GlobalConnectionIdManager();

        Connection connection = mock(Connection.class);

        assertThat(idManager1.getId(connection)).isEqualTo("1");
        assertThat(idManager2.getId(connection)).isEqualTo("2");
        assertThat(idManager1.getId(connection)).isEqualTo("3");
        assertThat(idManager2.getId(connection)).isEqualTo("4");
    }

    @Test
    public void getOpenConnectionIds() {
        GlobalConnectionIdManager idManager = new GlobalConnectionIdManager();
        assertThat(idManager.getOpenConnectionIds()).isEmpty();

        Connection connection = mock(Connection.class);

        String id1 = idManager.getId(connection);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1);

        String id2 = idManager.getId(connection);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1, id2);

        idManager.addClosedId(id2);
        assertThat(idManager.getOpenConnectionIds()).containsExactly(id1);

        idManager.addClosedId(id1);
        assertThat(idManager.getOpenConnectionIds()).isEmpty();
    }

}
