package net.ttddyy.dsproxy.test.hamcrest;

import net.ttddyy.dsproxy.test.BatchExecutionEntry;
import net.ttddyy.dsproxy.test.BatchParameterHolder;
import net.ttddyy.dsproxy.test.ParameterHolder;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;


/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public class BatchParameterHolderAssertions {

    public static Matcher<? super BatchParameterHolder> batchSize(final int batchSize) {
        return new FeatureMatcher<BatchParameterHolder, Integer>(equalTo(batchSize), "batchSize", "batchSize") {
            @Override
            protected Integer featureValueOf(BatchParameterHolder actual) {
                return actual.getBatchExecutionEntries().size();
            }
        };
    }


    public static Matcher<? super BatchParameterHolder> batch(final int index, final Matcher<? super ParameterHolder> parameterHolderMatcher) {
        return new CompositeMatcher<BatchParameterHolder, ParameterHolder>(parameterHolderMatcher) {
            @Override
            protected boolean validateByThisMatcher(BatchParameterHolder item, Description expected, Description actual) {
                List<BatchExecutionEntry> entries = item.getBatchExecutionEntries();
                int size = entries.size();
                if (size - 1 < index) {
                    expected.appendText("batch[" + index + "] exists");
                    actual.appendText("batch[] size was " + size);
                    return false;
                }

                BatchExecutionEntry entry = entries.get(index);
                if (!(entry instanceof ParameterHolder)) {  // TODO: unit test
                    expected.appendText("batch[" + index + "] an instance of " + ParameterHolder.class.getSimpleName());
                    actual.appendText("batch[" + index + "] is a " + item.getClass().getName());
                    return false;
                }

                return true;
            }

            @Override
            public ParameterHolder getValue(BatchParameterHolder actual) {
                BatchExecutionEntry entry = actual.getBatchExecutionEntries().get(index);
                return (ParameterHolder) entry;
            }

            @Override
            public String getSubMatcherFailureDescriptionPrefix() {
                return "batch[" + index + "] ";
            }
        };

    }


    // TODO: alias methods
    // preparedBatch(
    // batchParamKeys  or preparedBatchParamKeys
}
