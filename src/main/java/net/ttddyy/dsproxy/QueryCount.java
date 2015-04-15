package net.ttddyy.dsproxy;

/**
 * Considered to be used under same thread.
 * If used by multiple threads, need to change int to AtomicInteger.
 *
 * @author Tadaya Tsuyukubo
 */
public class QueryCount {

    // num of queries
    private int select;
    private int insert;
    private int update;
    private int delete;
    private int other;

    // num of statement type
    private int statement;
    private int prepared;
    private int callable;

    // num of database call
    private int total;
    private int failure;
    private int success;

    private long time;

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
        select++;
    }

    public void incrementInsert() {
        insert++;
    }

    public void incrementUpdate() {
        update++;
    }

    public void incrementDelete() {
        delete++;
    }

    public void incrementOther() {
        other++;
    }

    public void incrementStatement() {
        statement++;
    }

    public void incrementPrepared() {
        prepared++;
    }

    public void incrementCallable() {
        callable++;
    }

    public void incrementTotal() {
        total++;
    }

    public void incrementSuccess() {
        success++;
    }

    public void incrementFailure() {
        failure++;
    }

    public void incrementTime(long delta) {
        time += delta;
    }

    public int getSelect() {
        return select;
    }

    public void setSelect(int select) {
        this.select = select;
    }

    public int getInsert() {
        return insert;
    }

    public void setInsert(int insert) {
        this.insert = insert;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getOther() {
        return other;
    }

    public void setOther(int other) {
        this.other = other;
    }

    public int getStatement() {
        return statement;
    }

    public void setStatement(int statement) {
        this.statement = statement;
    }

    public int getPrepared() {
        return prepared;
    }

    public void setPrepared(int prepared) {
        this.prepared = prepared;
    }

    public int getCallable() {
        return callable;
    }

    public void setCallable(int callable) {
        this.callable = callable;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
