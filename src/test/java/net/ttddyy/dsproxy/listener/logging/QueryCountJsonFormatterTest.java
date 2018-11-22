package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.count.QueryCount;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryCountJsonFormatterTest {

    @Test
    void showAll() {
        QueryCount queryCount = new QueryCount();
        queryCount.setTime(1);
        queryCount.setTotal(2);
        queryCount.setSuccess(3);
        queryCount.setFailure(4);
        queryCount.setSelect(5);
        queryCount.setInsert(6);
        queryCount.setUpdate(7);
        queryCount.setDelete(8);
        queryCount.setOther(9);
        queryCount.setStatement(10);
        queryCount.setPrepared(11);
        queryCount.setCallable(12);

        QueryCountJsonFormatter formatter = QueryCountJsonFormatter.showAll();
        String entry = formatter.format(queryCount);

        assertThat(entry).isEqualTo("{\"time\":1, \"total\":2, \"success\":3, \"failure\":4, \"select\":5, \"insert\":6, \"update\":7, \"delete\":8, \"other\":9, \"statement\":10, \"prepared\":11, \"callable\":12}");
    }

    @Test
    void showTime() {
        QueryCount queryCount = new QueryCount();
        queryCount.setTime(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showTime();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"time\":1}");
    }

    @Test
    void showTotal() {
        QueryCount queryCount = new QueryCount();
        queryCount.setTotal(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showTotal();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"total\":1}");
    }

    @Test
    void showSuccess() {
        QueryCount queryCount = new QueryCount();
        queryCount.setSuccess(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showSuccess();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"success\":1}");
    }

    @Test
    void showFailure() {
        QueryCount queryCount = new QueryCount();
        queryCount.setFailure(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showFailure();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"failure\":1}");
    }

    @Test
    void showSelect() {
        QueryCount queryCount = new QueryCount();
        queryCount.setSelect(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showSelect();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"select\":1}");
    }

    @Test
    void showInsert() {
        QueryCount queryCount = new QueryCount();
        queryCount.setInsert(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showInsert();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"insert\":1}");
    }

    @Test
    void showUpdate() {
        QueryCount queryCount = new QueryCount();
        queryCount.setUpdate(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showUpdate();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"update\":1}");
    }

    @Test
    void showDelete() {
        QueryCount queryCount = new QueryCount();
        queryCount.setDelete(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showDelete();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"delete\":1}");
    }

    @Test
    void showOther() {
        QueryCount queryCount = new QueryCount();
        queryCount.setOther(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showOther();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"other\":1}");
    }

    @Test
    void showStatement() {
        QueryCount queryCount = new QueryCount();
        queryCount.setStatement(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showStatement();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"statement\":1}");
    }

    @Test
    void showPrepared() {
        QueryCount queryCount = new QueryCount();
        queryCount.setPrepared(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showPrepared();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"prepared\":1}");
    }

    @Test
    void showCallable() {
        QueryCount queryCount = new QueryCount();
        queryCount.setCallable(1);

        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
        formatter.showCallable();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("{\"callable\":1}");
    }

}
