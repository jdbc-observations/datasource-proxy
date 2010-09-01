package com.tpon.dsproxy;

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
    private int call;
    private int failure;

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

    public void incrementCall() {
        call++;
    }

    public void incrementFailure() {
        failure++;
    }

    public void incrementElapsedTime(long delta) {
        elapsedTime += delta;
    }

    public int getTotalNumOfQuery() {
        return select + insert + update + delete + other;
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

    public int getCall() {
        return call;
    }

    public void setCall(int call) {
        this.call = call;
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
