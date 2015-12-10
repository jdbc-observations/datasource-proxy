package net.ttddyy.dsproxy.test.assertj;

import net.ttddyy.dsproxy.test.BatchExecution;
import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.CallableBatchExecution;
import net.ttddyy.dsproxy.test.CallableExecution;
import net.ttddyy.dsproxy.test.ProxyTestDataSource;
import net.ttddyy.dsproxy.test.QueriesHolder;
import net.ttddyy.dsproxy.test.QueryExecution;
import net.ttddyy.dsproxy.test.StatementBatchExecution;
import net.ttddyy.dsproxy.test.StatementExecution;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Types;

import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameter.nullParam;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameter.outParam;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameter.param;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameter.paramAsString;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameters.containsNullParams;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameters.containsOutParams;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameters.containsParamIndexes;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameters.containsParamKeys;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameters.containsParamNames;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.BatchParameters.containsParams;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.CallableParameter.param;
import static net.ttddyy.dsproxy.test.assertj.CallableBatchExecutionAssert.CallableParameter.paramAsString;
import static net.ttddyy.dsproxy.test.assertj.DataSourceProxyAssertions.assertThat;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameter.nullParam;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameter.outParam;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameter.param;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameters.containsParamIndexes;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameters.containsParamKeys;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameters.containsParamNames;
import static net.ttddyy.dsproxy.test.assertj.data.BatchParameters.containsParams;
import static net.ttddyy.dsproxy.test.hamcrest.DataSourceProxyMatchers.batch;
import static net.ttddyy.dsproxy.test.hamcrest.ParameterHolderAssertions.paramIndexes;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 */
public class SampleAssertJAPI {

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

    public void test() {
        QueryExecution qe = null;
        BatchParameterHolder bph = null;
        Assert.assertThat(qe, batch());
        Assert.assertThat(bph, batch(1, paramIndexes(1)));


        // assertThat(extractBatch(ds)).

    }


    public void callableExecution(){
        CallableExecution ce = new CallableExecution();

        // null, outparam
        assertThat(ce).success();
        assertThat(ce).failure();

        assertThat(ce).param("key", "value").paramAsString("key", "value");
        assertThat(ce).param(10, "value").paramAsString(10, "value");
        assertThat(ce).param(atIndex(10), "value").paramAsString(atIndex(10), "value");
        assertThat(ce).outParam(10, "value").outParamAsString(10, "value");
        assertThat(ce).nullParam(10);

        assertThat(ce).containsParams(param("key", "value"), paramAsString("key", "value"));
        assertThat(ce).containsOutParams(param("key", "value"), paramAsString("key", "value"));
        assertThat(ce).containsNullParams(param("key", "value"), paramAsString("key", "value"));

//        assertThat(ce).batch(0).assertParamAsString(10).isEqualTo("");// string assert

    }
    public void callableBatchExecution(){
        CallableBatchExecution cbe = new CallableBatchExecution();

        assertThat(cbe).success();
        assertThat(cbe).failure();
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
