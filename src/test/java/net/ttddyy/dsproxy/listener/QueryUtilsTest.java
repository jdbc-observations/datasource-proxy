package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
class QueryUtilsTest {

    @Test
    void removeCommentAndWhiteSpace(){
        assertThat(QueryUtils.removeCommentAndWhiteSpace("")).isEmpty();
        assertThat(QueryUtils.removeCommentAndWhiteSpace(null)).isNull();
        assertThat(QueryUtils.removeCommentAndWhiteSpace("--\n")).isEmpty();
        assertThat(QueryUtils.removeCommentAndWhiteSpace("\n\n\n")).isEmpty();
        assertThat(QueryUtils.removeCommentAndWhiteSpace("/* abc  */")).isEmpty();
        assertThat(QueryUtils.removeCommentAndWhiteSpace("  ")).isEmpty();
        assertThat(QueryUtils.removeCommentAndWhiteSpace("  aa  ")).isEqualTo("aa");
        assertThat(QueryUtils.removeCommentAndWhiteSpace(" aa")).isEqualTo("aa");
        assertThat(QueryUtils.removeCommentAndWhiteSpace("aa ")).isEqualTo("aa");
    }

    @Test
    void getQueryType(){
        assertThat(QueryUtils.getQueryType("")).isEqualTo(QueryType.OTHER);
        assertThat(QueryUtils.getQueryType(null)).isEqualTo(QueryType.OTHER);
        assertThat(QueryUtils.getQueryType("SELECT")).isEqualTo(QueryType.SELECT);
        assertThat(QueryUtils.getQueryType("select")).isEqualTo(QueryType.SELECT);
        assertThat(QueryUtils.getQueryType("INSERT")).isEqualTo(QueryType.INSERT);
        assertThat(QueryUtils.getQueryType("insert")).isEqualTo(QueryType.INSERT);
        assertThat(QueryUtils.getQueryType("UPDATE")).isEqualTo(QueryType.UPDATE);
        assertThat(QueryUtils.getQueryType("update")).isEqualTo(QueryType.UPDATE);
        assertThat(QueryUtils.getQueryType("DELETE")).isEqualTo(QueryType.DELETE);
        assertThat(QueryUtils.getQueryType("delete")).isEqualTo(QueryType.DELETE);
    }
}
