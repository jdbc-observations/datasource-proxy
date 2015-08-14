package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public enum ExecutionType {

    IS_BATCH("batch", StatementBatchExecution.class, PreparedBatchExecution.class, CallableBatchExecution.class),
    IS_STATEMENT("statement", StatementExecution.class),
    IS_BATCH_STATEMENT("batch statement", StatementBatchExecution.class),
    IS_STATEMENT_OR_BATCH_STATEMENT("statement or batch statement", StatementExecution.class, StatementBatchExecution.class),
    IS_PREPARED("prepared", PreparedExecution.class),
    IS_BATCH_PREPARED("batch prepared", PreparedBatchExecution.class),
    IS_PREPARED_OR_BATCH_PREPARED("prepared or batch prepared", PreparedExecution.class, PreparedBatchExecution.class),
    IS_CALLABLE("callable", CallableExecution.class),
    IS_BATCH_CALLABLE("batch callable", CallableBatchExecution.class),
    IS_CALLABLE_OR_BATCH_CALLABLE("callable or batch callable", CallableExecution.class, CallableBatchExecution.class);


    private String description;
    private Class<? extends QueryExecution>[] executionTypes;

    ExecutionType(String description, Class<? extends QueryExecution>... executionTypes) {
        this.description = description;
        this.executionTypes = executionTypes;
    }

    public static ExecutionType valueOf(QueryExecution queryExecution) {
        if (queryExecution instanceof StatementExecution) {
            return IS_STATEMENT;
        } else if (queryExecution instanceof StatementBatchExecution) {
            return IS_BATCH_STATEMENT;
        } else if (queryExecution instanceof PreparedExecution) {
            return IS_PREPARED;
        } else if (queryExecution instanceof PreparedBatchExecution) {
            return IS_BATCH_PREPARED;
        } else if (queryExecution instanceof CallableExecution) {
            return IS_CALLABLE;
        } else if (queryExecution instanceof CallableBatchExecution) {
            return IS_BATCH_CALLABLE;
        }
        return null;
    }

    public String getDescription() {
        return description;
    }

    public Class<? extends QueryExecution>[] getExecutionTypes() {
        return executionTypes;
    }

}
