package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.count.QueryCount;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Convert {@link QueryCount} to {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class QueryCountFormatter extends AbstractFormatterSupport<QueryCount> implements Function<QueryCount, String> {

    private List<BiConsumer<QueryCount, StringBuilder>> consumers = new ArrayList<>();

    public QueryCountFormatter addConsumer(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public QueryCountFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    private BiConsumer<QueryCount, StringBuilder> onTime = (queryCount, sb) -> {
        sb.append("Time:");
        sb.append(queryCount.getTime());
    };
    private BiConsumer<QueryCount, StringBuilder> onTotal = (queryCount, sb) -> {
        sb.append("Total:");
        sb.append(queryCount.getTotal());
    };
    private BiConsumer<QueryCount, StringBuilder> onSuccess = (queryCount, sb) -> {
        sb.append("Success:");
        sb.append(queryCount.getSuccess());
    };
    private BiConsumer<QueryCount, StringBuilder> onFailure = (queryCount, sb) -> {
        sb.append("Failure:");
        sb.append(queryCount.getFailure());
    };
    private BiConsumer<QueryCount, StringBuilder> onSelect = (queryCount, sb) -> {
        sb.append("Select:");
        sb.append(queryCount.getSelect());
    };
    private BiConsumer<QueryCount, StringBuilder> onInsert = (queryCount, sb) -> {
        sb.append("Insert:");
        sb.append(queryCount.getInsert());
    };
    private BiConsumer<QueryCount, StringBuilder> onUpdate = (queryCount, sb) -> {
        sb.append("Update:");
        sb.append(queryCount.getUpdate());
    };
    private BiConsumer<QueryCount, StringBuilder> onDelete = (queryCount, sb) -> {
        sb.append("Delete:");
        sb.append(queryCount.getDelete());
    };
    private BiConsumer<QueryCount, StringBuilder> onOther = (queryCount, sb) -> {
        sb.append("Other:");
        sb.append(queryCount.getOther());
    };
    private BiConsumer<QueryCount, StringBuilder> onStatement = (queryCount, sb) -> {
        sb.append("Statement:");
        sb.append(queryCount.getStatement());
    };
    private BiConsumer<QueryCount, StringBuilder> onPrepared = (queryCount, sb) -> {
        sb.append("Prepared:");
        sb.append(queryCount.getPrepared());
    };
    private BiConsumer<QueryCount, StringBuilder> onCallable = (queryCount, sb) -> {
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

    @Override
    public String apply(QueryCount queryCount) {
        return format(queryCount);
    }

    public String format(QueryCount queryCount) {

        StringBuilder sb = new StringBuilder();

        this.consumers.forEach(consumer -> {

            // if it is newLine, remove previously added delimiter
            if (consumer == this.newLine) {
                chompIfEndWith(sb, this.delimiter);
            }

            consumer.accept(queryCount, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        });

        chompIfEndWith(sb, this.delimiter);

        return sb.toString();

    }

    public QueryCountFormatter showTime() {
        this.addConsumer(this.onTime);
        return this;
    }

    public QueryCountFormatter showTime(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onTime = consumer;
        return showTime();
    }

    public QueryCountFormatter showTotal() {
        this.addConsumer(this.onTotal);
        return this;
    }

    public QueryCountFormatter showTotal(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onTotal = consumer;
        return showTotal();
    }


    public QueryCountFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public QueryCountFormatter showSuccess(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public QueryCountFormatter showFailure() {
        this.addConsumer(this.onFailure);
        return this;
    }

    public QueryCountFormatter showFailure(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onFailure = consumer;
        return showFailure();
    }

    public QueryCountFormatter showSelect() {
        this.addConsumer(this.onSelect);
        return this;
    }

    public QueryCountFormatter showSelect(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onSelect = consumer;
        return showSelect();
    }

    public QueryCountFormatter showInsert() {
        this.addConsumer(this.onInsert);
        return this;
    }

    public QueryCountFormatter showInsert(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onInsert = consumer;
        return showInsert();
    }

    public QueryCountFormatter showUpdate() {
        this.addConsumer(this.onUpdate);
        return this;
    }

    public QueryCountFormatter showUpdate(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onUpdate = consumer;
        return showUpdate();
    }

    public QueryCountFormatter showDelete() {
        this.addConsumer(this.onDelete);
        return this;
    }

    public QueryCountFormatter showDelete(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onDelete = consumer;
        return showDelete();
    }

    public QueryCountFormatter showOther() {
        this.addConsumer(this.onOther);
        return this;
    }

    public QueryCountFormatter showOther(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onOther = consumer;
        return showOther();
    }


    public QueryCountFormatter showStatement() {
        this.addConsumer(this.onStatement);
        return this;
    }

    public QueryCountFormatter showStatement(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onStatement = consumer;
        return showStatement();
    }

    public QueryCountFormatter showPrepared() {
        this.addConsumer(this.onPrepared);
        return this;
    }

    public QueryCountFormatter showPrepared(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onPrepared = consumer;
        return showPrepared();
    }

    public QueryCountFormatter showCallable() {
        this.addConsumer(this.onCallable);
        return this;
    }

    public QueryCountFormatter showCallable(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onCallable = consumer;
        return showCallable();
    }

}
