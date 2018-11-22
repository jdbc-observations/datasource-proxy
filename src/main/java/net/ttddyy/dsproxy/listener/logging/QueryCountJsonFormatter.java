package net.ttddyy.dsproxy.listener.logging;

import net.ttddyy.dsproxy.listener.count.QueryCount;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Convert {@link QueryCount} to json {@code String}.
 *
 * @author Tadaya Tsuyukubo
 * @since 2.0
 */
public class QueryCountJsonFormatter extends AbstractFormatterSupport<QueryCount> implements Function<QueryCount, String> {

    private List<BiConsumer<QueryCount, StringBuilder>> consumers = new ArrayList<>();

    public QueryCountJsonFormatter addConsumer(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.consumers.add(consumer);
        return this;
    }

    public QueryCountJsonFormatter newLine() {
        this.consumers.add(this.newLine);
        return this;
    }

    private BiConsumer<QueryCount, StringBuilder> onTime = (queryCount, sb) -> {
        sb.append("\"time\":");
        sb.append(queryCount.getTime());
    };
    private BiConsumer<QueryCount, StringBuilder> onTotal = (queryCount, sb) -> {
        sb.append("\"total\":");
        sb.append(queryCount.getTotal());
    };
    private BiConsumer<QueryCount, StringBuilder> onSuccess = (queryCount, sb) -> {
        sb.append("\"success\":");
        sb.append(queryCount.getSuccess());
    };
    private BiConsumer<QueryCount, StringBuilder> onFailure = (queryCount, sb) -> {
        sb.append("\"failure\":");
        sb.append(queryCount.getFailure());
    };
    private BiConsumer<QueryCount, StringBuilder> onSelect = (queryCount, sb) -> {
        sb.append("\"select\":");
        sb.append(queryCount.getSelect());
    };
    private BiConsumer<QueryCount, StringBuilder> onInsert = (queryCount, sb) -> {
        sb.append("\"insert\":");
        sb.append(queryCount.getInsert());
    };
    private BiConsumer<QueryCount, StringBuilder> onUpdate = (queryCount, sb) -> {
        sb.append("\"update\":");
        sb.append(queryCount.getUpdate());
    };
    private BiConsumer<QueryCount, StringBuilder> onDelete = (queryCount, sb) -> {
        sb.append("\"delete\":");
        sb.append(queryCount.getDelete());
    };
    private BiConsumer<QueryCount, StringBuilder> onOther = (queryCount, sb) -> {
        sb.append("\"other\":");
        sb.append(queryCount.getOther());
    };
    private BiConsumer<QueryCount, StringBuilder> onStatement = (queryCount, sb) -> {
        sb.append("\"statement\":");
        sb.append(queryCount.getStatement());
    };
    private BiConsumer<QueryCount, StringBuilder> onPrepared = (queryCount, sb) -> {
        sb.append("\"prepared\":");
        sb.append(queryCount.getPrepared());
    };
    private BiConsumer<QueryCount, StringBuilder> onCallable = (queryCount, sb) -> {
        sb.append("\"callable\":");
        sb.append(queryCount.getCallable());
    };


    public static QueryCountJsonFormatter showAll() {
        QueryCountJsonFormatter formatter = new QueryCountJsonFormatter();
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

        sb.append("{");
        this.consumers.forEach(consumer -> {

            consumer.accept(queryCount, sb);

            if (consumer != this.newLine) {
                sb.append(this.delimiter);
            }
        });

        chompIfEndWith(sb, this.delimiter);

        sb.append("}");

        return sb.toString();

    }

    public QueryCountJsonFormatter showTime() {
        this.addConsumer(this.onTime);
        return this;
    }

    public QueryCountJsonFormatter showTime(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onTime = consumer;
        return showTime();
    }

    public QueryCountJsonFormatter showTotal() {
        this.addConsumer(this.onTotal);
        return this;
    }

    public QueryCountJsonFormatter showTotal(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onTotal = consumer;
        return showTotal();
    }


    public QueryCountJsonFormatter showSuccess() {
        this.addConsumer(this.onSuccess);
        return this;
    }

    public QueryCountJsonFormatter showSuccess(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onSuccess = consumer;
        return showSuccess();
    }

    public QueryCountJsonFormatter showFailure() {
        this.addConsumer(this.onFailure);
        return this;
    }

    public QueryCountJsonFormatter showFailure(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onFailure = consumer;
        return showFailure();
    }

    public QueryCountJsonFormatter showSelect() {
        this.addConsumer(this.onSelect);
        return this;
    }

    public QueryCountJsonFormatter showSelect(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onSelect = consumer;
        return showSelect();
    }

    public QueryCountJsonFormatter showInsert() {
        this.addConsumer(this.onInsert);
        return this;
    }

    public QueryCountJsonFormatter showInsert(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onInsert = consumer;
        return showInsert();
    }

    public QueryCountJsonFormatter showUpdate() {
        this.addConsumer(this.onUpdate);
        return this;
    }

    public QueryCountJsonFormatter showUpdate(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onUpdate = consumer;
        return showUpdate();
    }

    public QueryCountJsonFormatter showDelete() {
        this.addConsumer(this.onDelete);
        return this;
    }

    public QueryCountJsonFormatter showDelete(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onDelete = consumer;
        return showDelete();
    }

    public QueryCountJsonFormatter showOther() {
        this.addConsumer(this.onOther);
        return this;
    }

    public QueryCountJsonFormatter showOther(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onOther = consumer;
        return showOther();
    }


    public QueryCountJsonFormatter showStatement() {
        this.addConsumer(this.onStatement);
        return this;
    }

    public QueryCountJsonFormatter showStatement(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onStatement = consumer;
        return showStatement();
    }

    public QueryCountJsonFormatter showPrepared() {
        this.addConsumer(this.onPrepared);
        return this;
    }

    public QueryCountJsonFormatter showPrepared(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onPrepared = consumer;
        return showPrepared();
    }

    public QueryCountJsonFormatter showCallable() {
        this.addConsumer(this.onCallable);
        return this;
    }

    public QueryCountJsonFormatter showCallable(BiConsumer<QueryCount, StringBuilder> consumer) {
        this.onCallable = consumer;
        return showCallable();
    }

}
