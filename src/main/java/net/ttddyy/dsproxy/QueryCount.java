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

    // num of database call
    private int total;
    private int failure;
    private int success;

    private long elapsedTime;

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

    public void incrementTotal() {
        total++;
    }

    public void incrementSuccess() {
        success++;
    }

    public void incrementFailure() {
        failure++;
    }

    public void incrementElapsedTime(long delta) {
        elapsedTime += delta;
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

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }
}
