package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.QueryCount;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryCountFormatterTest {

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

        QueryCountFormatter formatter = QueryCountFormatter.showAll();
        String entry = formatter.format(queryCount);

        assertThat(entry).isEqualTo("Time:1, Total:2, Success:3, Failure:4, Select:5, Insert:6, Update:7, Delete:8, Other:9, Statement:10, Prepared:11, Callable:12");
    }

    @Test
    void showTime() {
        QueryCount queryCount = new QueryCount();
        queryCount.setTime(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showTime();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Time:1");
    }

    @Test
    void showTotal() {
        QueryCount queryCount = new QueryCount();
        queryCount.setTotal(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showTotal();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Total:1");
    }

    @Test
    void showSuccess() {
        QueryCount queryCount = new QueryCount();
        queryCount.setSuccess(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showSuccess();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Success:1");
    }

    @Test
    void showFailure() {
        QueryCount queryCount = new QueryCount();
        queryCount.setFailure(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showFailure();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Failure:1");
    }

    @Test
    void showSelect() {
        QueryCount queryCount = new QueryCount();
        queryCount.setSelect(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showSelect();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Select:1");
    }

    @Test
    void showInsert() {
        QueryCount queryCount = new QueryCount();
        queryCount.setInsert(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showInsert();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Insert:1");
    }

    @Test
    void showUpdate() {
        QueryCount queryCount = new QueryCount();
        queryCount.setUpdate(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showUpdate();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Update:1");
    }

    @Test
    void showDelete() {
        QueryCount queryCount = new QueryCount();
        queryCount.setDelete(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showDelete();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Delete:1");
    }

    @Test
    void showOther() {
        QueryCount queryCount = new QueryCount();
        queryCount.setOther(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showOther();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Other:1");
    }

    @Test
    void showStatement() {
        QueryCount queryCount = new QueryCount();
        queryCount.setStatement(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showStatement();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Statement:1");
    }

    @Test
    void showPrepared() {
        QueryCount queryCount = new QueryCount();
        queryCount.setPrepared(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showPrepared();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Prepared:1");
    }

    @Test
    void showCallable() {
        QueryCount queryCount = new QueryCount();
        queryCount.setCallable(1);

        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.showCallable();

        String entry = formatter.format(queryCount);
        assertThat(entry).isEqualTo("Callable:1");
    }

}
