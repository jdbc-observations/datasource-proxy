package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.QueryType;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.PreparedBatchExecution;
import net.ttddyy.dsproxy.test.PreparedExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.junit.Assert;

import java.sql.Types;

import static net.ttddyy.dsproxy.test.assertj.DataSourceProxyAssertions.assertThat;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.nullParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.outParam;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameter.param;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamIndexes;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamKeys;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamNames;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParamValuesExactly;
import static net.ttddyy.dsproxy.test.assertj.data.ExecutionParameters.containsParams;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.batch;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

/**
 * @author Tadaya Tsuyukubo
 */
public class SampleAssertJAPI {

/*
    @Test
    public void aa() {
        QueryExecution qe = new StatementExecution();
        QueriesHolder queriesHolder = new StatementBatchExecution();
        StatementBatchExecution sbe = new StatementBatchExecution();
        CallableBatchExecution cbe = new CallableBatchExecution();

        ProxyTestDataSource ds = new ProxyTestDataSource();
        ds.getQueryExecutions().add(qe);

        new ProxyTestDataSourceAssert(ds).hasExecutionCount(10);

        assertThat(sbe).queries();
        Assertions.assertThat("String").startsWith("");

        assertThat(cbe.getQuery()).startsWith("ABC");
        assertThat(cbe).batch(0).paramAsString("key", "value").paramAs("foo", String.class, "");
        BatchExecutionEntry be = cbe.getBatchExecutionEntries().get(0);

        assertThat(be).params();

    }
*/

    public void test() {
        QueryExecution qe = null;
        BatchParameterHolder bph = null;
        Assert.assertThat(qe, batch());
        Assert.assertThat(bph, batch(1, paramIndexes(1)));


        // assertThat(extractBatch(ds)).

    }

    private void testDataSource() {
        ProxyTestDataSource ds = new ProxyTestDataSource();

        // execution count
        assertThat(ds).hasExecutionCount(3);
        assertThat(ds).hasStatementCount(3);
        assertThat(ds).hasBatchStatementCount(3);
        assertThat(ds).hasStatementOrBatchStatementCount(3);
        assertThat(ds).hasPreparedCount(3);
        assertThat(ds).hasBatchPreparedCount(3);
        assertThat(ds).hasPreparedOrBatchPreparedCount(3);
        assertThat(ds).hasCallableCount(3);
        assertThat(ds).hasBatchCallableCount(3);
        assertThat(ds).hasCallableOrBatchCallableCount(3);



//        // each execution
//        assertThat(ds, executions(0, isBatch()));
//        assertThat(ds, executions(0, batch()));
//        assertThat(ds, executions(0, is(batch())));
//
//        assertThat(ds, executions(0, isStatement()));
//        assertThat(ds, executions(0, statement()));
//        assertThat(ds, executions(0, is(isStatement())));
//
//        assertThat(ds, executions(0, isBatchStatement()));
//        assertThat(ds, executions(0, batchStatement()));
//        assertThat(ds, executions(0, is(batchStatement())));
//
//        assertThat(ds, executions(0, isStatementOrBatchStatement()));
//        assertThat(ds, executions(0, isPrepared()));
//        assertThat(ds, executions(0, isBatchPrepared()));
//        assertThat(ds, executions(0, isPreparedOrBatchPrepared()));
//        assertThat(ds, executions(0, isCallable()));
//        assertThat(ds, executions(0, isBatchCallable()));
//        assertThat(ds, executions(0, isCallableOrBatchCallable()));
//
//        assertThat(ds, executions(0, is(success())));
//
//
//        // query count
//        assertThat(ds, totalQueryCount(5));
//        assertThat(ds, selectCount(3));
//        assertThat(ds, insertCount(3));
//        assertThat(ds, allOf(updateCount(3), deleteCount(1)));
//
//        // TODO:
//        // ds.reset();
//
//        assertThat(ds.getFirstStatement(), query(is("abc")));
//        assertThat(ds.getFirstBatchStatement(), queries(0, is("abc")));
//        assertThat(ds.getFirstBatchCallable(), query(is("abc")));
//

        assertThat(ds.getQueryExecutions().get(0)).isSuccess();
        assertThat(ds.getQueryExecutions().get(0)).isFailure();

        assertThat(ds.getQueryExecutions().get(0)).isBatch();
        assertThat(ds.getQueryExecutions().get(0)).isStatement();
        assertThat(ds.getQueryExecutions().get(0)).isBatchStatement();
        assertThat(ds.getQueryExecutions().get(0)).isStatementOrBatchStatement();
        assertThat(ds.getQueryExecutions().get(0)).isPrepared();
        assertThat(ds.getQueryExecutions().get(0)).isBatchPrepared();
        assertThat(ds.getQueryExecutions().get(0)).isPreparedOrBatchPrepared();
        assertThat(ds.getQueryExecutions().get(0)).isCallable();
        assertThat(ds.getQueryExecutions().get(0)).isBatchCallable();
        assertThat(ds.getQueryExecutions().get(0)).isCallableOrBatchCallable();

        assertThat(ds.getQueryExecutions().get(0)).asStatement();
        assertThat(ds.getQueryExecutions().get(0)).asBatchStatement();
        assertThat(ds.getQueryExecutions().get(0)).asPrepared();
        assertThat(ds.getQueryExecutions().get(0)).asBatchPrepared();
        assertThat(ds.getQueryExecutions().get(0)).asCallable();
        assertThat(ds.getQueryExecutions().get(0)).asBatchCallable();
    }


    private void statementExecution() {
        StatementExecution se = new StatementExecution();

        assertThat(se).isSuccess();
        assertThat(se).isFailure();

        assertThat(se.getQuery()).isEqualTo("...");
        assertThat(se).hasQueryType(QueryType.SELECT);
    }

    private void batchStatementExecution() {
        StatementBatchExecution sbe = new StatementBatchExecution();

        assertThat(sbe).isSuccess();
        assertThat(sbe).isFailure();

        // check batch queries
        assertThat(sbe.getQueries().get(0)).isEqualTo("...");
        assertThat(sbe).contains(QueryType.SELECT, atIndex(0));
    }

    public void preparedExecution() {
        PreparedExecution pe = new PreparedExecution();

        assertThat(pe).isSuccess();
        assertThat(pe).isFailure();

        assertThat(pe).containsParam(10, "value").containsParam(10, "value");
        assertThat(pe).containsParams(param(10, "value"), param(10, "value"), nullParam(12));
        assertThat(pe).containsParamIndex(10);
        assertThat(pe).containsParamIndexes(10, 11);
        assertThat(pe).containsParamValuesExactly("value", 100, null, 12);

        assertThat(pe.getQuery()).isEqualTo("...");
    }

    public void preparedBatchExecution() {
        PreparedBatchExecution pbe = new PreparedBatchExecution();

        assertThat(pbe).isSuccess();
        assertThat(pbe).isFailure();

        assertThat(pbe).hasBatchSize(3);

        assertThat(pbe).batch(0, containsParams(param(10, "value"), param(11, 100), nullParam(12)));
        assertThat(pbe).batch(0, containsParamValuesExactly("value", 100, null, 12));

        assertThat(pbe).batch(0, containsParamIndexes(10, 11));

        assertThat(pbe).batch(0).containsParam(10, "value").containsParam(10, "value");
        assertThat(pbe).batch(0).containsParams(param(10, "value"), param(10, "value"), nullParam(12));
        assertThat(pbe).batch(0).containsParamIndex(10);
        assertThat(pbe).batch(0).containsParamValuesExactly("value", 100, null, 12);

        assertThat(pbe.getQuery()).isEqualTo("...");
    }


    public void callableExecution() {
        CallableExecution ce = new CallableExecution();

        assertThat(ce).isSuccess();
        assertThat(ce).isFailure();

        assertThat(ce).containsParam("key", "value")
                .containsParam(10, "value")
                .containsOutParam(10, Types.INTEGER)
                .containsNullParam(10);
//        assertThat(ce).containsParam(atIndex(10), "value");

        assertThat(ce).containsParams(param("key", "value"), param(10, "value"), param("a", 100), outParam("key", Types.INTEGER), nullParam("key"));
        assertThat(ce).containsParamKey("key").containsParamKey(10).containsParamIndex(10).containsParamName("key");
        assertThat(ce).containsParamKeys("key", 10).containsParamIndexes(10, 11).containsParamNames("key", "key");

//        assertThat(ce).containsOutParams(param("key", "value"), paramAsString("key", "value"));
//        assertThat(ce).containsNullParams(param("key", "value"), paramAsString("key", "value"));

    }

    public void callableBatchExecution() {
        CallableBatchExecution cbe = new CallableBatchExecution();

        assertThat(cbe).isSuccess();
        assertThat(cbe).isFailure();

        assertThat(cbe).hasBatchSize(3);

//        assertThat(cbe).batch(0).param("key", "value").paramAsString("key", "value");
//        assertThat(cbe).batch(0).param(10, "value").paramAsString(10, "value");
//        assertThat(cbe).batch(0).outParam(10, "value").outParamAsString(10, "value");
//        assertThat(cbe).batch(0).paramSetNull(10);

//        assertThat(cbe).batch(0, param("key", "value"), paramAsString("key", "value"));
        assertThat(cbe).batch(0, containsParams(param("key", "value"), param(10, "value"), param("a", 100), outParam("key", Types.INTEGER), nullParam("key")));
//        assertThat(cbe).batch(0, containsParams(param("key", "value"), param(10, "value")));
//        assertThat(cbe).batch(0, containsOutParams(param("key", Types.INTEGER), param(10, Types.INTEGER)));
//        assertThat(cbe).batch(0, containsNullParams(param("key", "value"), param(10, "value")));

        assertThat(cbe).batch(0, containsParamKeys("key", 10));
        assertThat(cbe).batch(0, containsParamIndexes(10, 11));
        assertThat(cbe).batch(0, containsParamNames("key", "key2"));

        assertThat(cbe).batch(0).containsParam("key", "value").containsParam(10, "value").containsOutParam("key", Types.INTEGER).containsNullParam("key");
        assertThat(cbe).batch(0).containsParams(param("key", "value"), param(10, "value"), param("a", 100), outParam("key", Types.INTEGER), nullParam("key"));
        assertThat(cbe).batch(0).containsParamKey("key").containsParamKey(10).containsParamIndex(10).containsParamName("key");
        assertThat(cbe).batch(0).containsParamKeys("key", 10).containsParamIndexes(10, 11).containsParamNames("key", "key");


//        assertThat(cbe).batch(0).assertParamAsString(10).isEqualTo("");// string assert

        assertThat(cbe.getQuery()).isEqualTo("...");

    }
}
