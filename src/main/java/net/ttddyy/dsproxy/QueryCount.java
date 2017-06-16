package net.ttddyy.dsproxy;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Hold query metrics.
 *
 * @author Tadaya Tsuyukubo
 */
public class QueryCount {

    // num of queries
    private AtomicLong select = new AtomicLong();
    private AtomicLong insert = new AtomicLong();
    private AtomicLong update = new AtomicLong();
    private AtomicLong delete = new AtomicLong();
    private AtomicLong other = new AtomicLong();

    // num of statement type
    private AtomicLong statement = new AtomicLong();
    private AtomicLong prepared = new AtomicLong();
    private AtomicLong callable = new AtomicLong();

    // num of database call
    private AtomicLong total = new AtomicLong();
    private AtomicLong failure = new AtomicLong();
    private AtomicLong success = new AtomicLong();

    private AtomicLong time = new AtomicLong();

    public void increment(QueryType queryType) {
        switch (queryType) {
            case SELECT:
                incrementSelect();
                break;
            case INSERT:
                incrementInsert();
                break;
            case UPDATE:
                incrementUpdate();
                break;
            case DELETE:
                incrementDelete();
                break;
            case OTHER:
                incrementOther();
        }
    }

    public void increment(StatementType statementType) {
        switch (statementType) {
            case STATEMENT:
                incrementStatement();
                break;
            case PREPARED:
                incrementPrepared();
                break;
            case CALLABLE:
                incrementCallable();
                break;
        }
    }

    public void incrementSelect() {
        select.incrementAndGet();
    }

    public void incrementInsert() {
        insert.incrementAndGet();
    }

    public void incrementUpdate() {
        update.incrementAndGet();
    }

    public void incrementDelete() {
        delete.incrementAndGet();
    }

    public void incrementOther() {
        other.incrementAndGet();
    }

    public void incrementStatement() {
        statement.incrementAndGet();
    }

    public void incrementPrepared() {
        prepared.incrementAndGet();
    }

    public void incrementCallable() {
        callable.incrementAndGet();
    }

    public void incrementTotal() {
        total.incrementAndGet();
    }

    public void incrementSuccess() {
        success.incrementAndGet();
    }

    public void incrementFailure() {
        failure.incrementAndGet();
    }

    public void incrementTime(long delta) {
        time.addAndGet(delta);
    }

    public long getSelect() {
        return select.longValue();
    }

    public void setSelect(long select) {
        this.select.set(select);
    }

    public long getInsert() {
        return insert.longValue();
    }

    public void setInsert(long insert) {
        this.insert.set(insert);
    }

    public long getUpdate() {
        return update.longValue();
    }

    public void setUpdate(long update) {
        this.update.set(update);
    }

    public long getDelete() {
        return delete.longValue();
    }

    public void setDelete(long delete) {
        this.delete.set(delete);
    }

    public long getOther() {
        return other.longValue();
    }

    public void setOther(long other) {
        this.other.set(other);
    }

    public long getStatement() {
        return statement.longValue();
    }

    public void setStatement(long statement) {
        this.statement.set(statement);
    }

    public long getPrepared() {
        return prepared.longValue();
    }

    public void setPrepared(long prepared) {
        this.prepared.set(prepared);
    }

    public long getCallable() {
        return callable.longValue();
    }

    public void setCallable(long callable) {
        this.callable.set(callable);
    }

    public long getTotal() {
        return total.longValue();
    }

    public void setTotal(long total) {
        this.total.set(total);
    }

    public long getSuccess() {
        return success.longValue();
    }

    public void setSuccess(long success) {
        this.success.set(success);
    }

    public long getFailure() {
        return failure.longValue();
    }

    public void setFailure(long failure) {
        this.failure.set(failure);
    }

    public long getTime() {
        return time.longValue();
    }

    public void setTime(long time) {
        this.time.set(time);
    }
}
