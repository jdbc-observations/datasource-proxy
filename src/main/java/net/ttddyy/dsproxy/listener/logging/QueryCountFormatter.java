package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.function.DSProxyBiConsumer;
import net.ttddyy.dsproxy.listener.count.QueryCount;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert {@link QueryCount} to {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class QueryCountFormatter extends AbstractFormatterSupport<QueryCount> {

    private List<DSProxyBiConsumer<QueryCount, StringBuilder>> consumers = new ArrayList<>();

    public QueryCountFormatter addConsumer(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public QueryCountFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    private DSProxyBiConsumer<QueryCount, StringBuilder> onTime = (queryCount, sb) -> {
        sb.append("Time:");
        sb.append(queryCount.getTime());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onTotal = (queryCount, sb) -> {
        sb.append("Total:");
        sb.append(queryCount.getTotal());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onSuccess = (queryCount, sb) -> {
        sb.append("Success:");
        sb.append(queryCount.getSuccess());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onFailure = (queryCount, sb) -> {
        sb.append("Failure:");
        sb.append(queryCount.getFailure());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onSelect = (queryCount, sb) -> {
        sb.append("Select:");
        sb.append(queryCount.getSelect());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onInsert = (queryCount, sb) -> {
        sb.append("Insert:");
        sb.append(queryCount.getInsert());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onUpdate = (queryCount, sb) -> {
        sb.append("Update:");
        sb.append(queryCount.getUpdate());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onDelete = (queryCount, sb) -> {
        sb.append("Delete:");
        sb.append(queryCount.getDelete());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onOther = (queryCount, sb) -> {
        sb.append("Other:");
        sb.append(queryCount.getOther());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onStatement = (queryCount, sb) -> {
        sb.append("Statement:");
        sb.append(queryCount.getStatement());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onPrepared = (queryCount, sb) -> {
        sb.append("Prepared:");
        sb.append(queryCount.getPrepared());
    };
    private DSProxyBiConsumer<QueryCount, StringBuilder> onCallable = (queryCount, sb) -> {
        sb.append("Callable:");
        sb.append(queryCount.getCallable());
    };


    public static QueryCountFormatter showAll() {
        QueryCountFormatter formatter = new QueryCountFormatter();
        formatter.addConsumer(formatter.onTime);
        formatter.addConsumer(formatter.onTotal);
        formatter.addConsumer(formatter.onSuccess);
        formatter.addConsumer(formatter.onFailure);
        formatter.addConsumer(formatter.onSelect);
        formatter.addConsumer(formatter.onInsert);
        formatter.addConsumer(formatter.onUpdate);
        formatter.addConsumer(formatter.onDelete);
        formatter.addConsumer(formatter.onOther);
        formatter.addConsumer(formatter.onStatement);
        formatter.addConsumer(formatter.onPrepared);
        formatter.addConsumer(formatter.onCallable);
        return formatter;
    }

    public String format(QueryCount queryCount) {

        StringBuilder sb = new StringBuilder();

        for (DSProxyBiConsumer<QueryCount, StringBuilder> consumer : consumers) {
            // if it is newLine, remove previously added delimiter
            if (consumer == this.newLine) {
                chompIfEndWith(sb, this.delimiter);
            }

            consumer.accept(queryCount, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        }

        chompIfEndWith(sb, this.delimiter);

        return sb.toString();

    }

    public QueryCountFormatter showTime() {
        this.addConsumer(this.onTime);
        return this;
    }

    public QueryCountFormatter showTime(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onTime = consumer;
        return showTime();
    }

    public QueryCountFormatter showTotal() {
        this.addConsumer(this.onTotal);
        return this;
    }

    public QueryCountFormatter showTotal(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onTotal = consumer;
        return showTotal();
    }


    public QueryCountFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public QueryCountFormatter showSuccess(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public QueryCountFormatter showFailure() {
        this.addConsumer(this.onFailure);
        return this;
    }

    public QueryCountFormatter showFailure(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onFailure = consumer;
        return showFailure();
    }

    public QueryCountFormatter showSelect() {
        this.addConsumer(this.onSelect);
        return this;
    }

    public QueryCountFormatter showSelect(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onSelect = consumer;
        return showSelect();
    }

    public QueryCountFormatter showInsert() {
        this.addConsumer(this.onInsert);
        return this;
    }

    public QueryCountFormatter showInsert(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onInsert = consumer;
        return showInsert();
    }

    public QueryCountFormatter showUpdate() {
        this.addConsumer(this.onUpdate);
        return this;
    }

    public QueryCountFormatter showUpdate(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onUpdate = consumer;
        return showUpdate();
    }

    public QueryCountFormatter showDelete() {
        this.addConsumer(this.onDelete);
        return this;
    }

    public QueryCountFormatter showDelete(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onDelete = consumer;
        return showDelete();
    }

    public QueryCountFormatter showOther() {
        this.addConsumer(this.onOther);
        return this;
    }

    public QueryCountFormatter showOther(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onOther = consumer;
        return showOther();
    }


    public QueryCountFormatter showStatement() {
        this.addConsumer(this.onStatement);
        return this;
    }

    public QueryCountFormatter showStatement(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onStatement = consumer;
        return showStatement();
    }

    public QueryCountFormatter showPrepared() {
        this.addConsumer(this.onPrepared);
        return this;
    }

    public QueryCountFormatter showPrepared(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onPrepared = consumer;
        return showPrepared();
    }

    public QueryCountFormatter showCallable() {
        this.addConsumer(this.onCallable);
        return this;
    }

    public QueryCountFormatter showCallable(DSProxyBiConsumer<QueryCount, StringBuilder> consumer) {
        this.onCallable = consumer;
        return showCallable();
    }

}
