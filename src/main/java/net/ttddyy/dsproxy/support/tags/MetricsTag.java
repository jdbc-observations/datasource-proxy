package net.ttddyy.dsproxy.support.tags;

import net.ttddyy.dsproxy.QueryCount;
import net.ttddyy.dsproxy.QueryCountHolder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

/**
 * Implementation of "metrics" custom tag.
 *
 * @author Tadaya Tsuyukubo
 */
public class MetricsTag extends SimpleTagSupport {
    private String dataSource;
    private String metric;

    @Override
    public void doTag() throws JspException, IOException {

        if (metric == null || "".equals(metric)) {
            return;
        }

        final QueryCount count;
        if (dataSource == null || "".equals(dataSource)) {
            count = QueryCountHolder.getGrandTotal();
        } else {
            count = QueryCountHolder.get(dataSource);
        }

        if (count == null) {
            return;
        }

        final StringBuilder sb = new StringBuilder();
        if ("select".equalsIgnoreCase(metric)) {
            sb.append(count.getSelect());
        } else if ("insert".equalsIgnoreCase(metric)) {
            sb.append(count.getInsert());
        } else if ("update".equalsIgnoreCase(metric)) {
            sb.append(count.getUpdate());
        } else if ("delete".equalsIgnoreCase(metric)) {
            sb.append(count.getDelete());
        } else if ("other".equalsIgnoreCase(metric)) {
            sb.append(count.getOther());
        } else if ("statement".equalsIgnoreCase(metric)) {
            sb.append(count.getStatement());
        } else if ("prepared".equalsIgnoreCase(metric)) {
            sb.append(count.getPrepared());
        } else if ("callable".equalsIgnoreCase(metric)) {
            sb.append(count.getCallable());
        } else if ("total".equalsIgnoreCase(metric)) {
            sb.append(count.getTotal());
        } else if ("success".equalsIgnoreCase(metric)) {
            sb.append(count.getSuccess());
        } else if ("failure".equalsIgnoreCase(metric)) {
            sb.append(count.getFailure());
        } else if ("time".equalsIgnoreCase(metric)) {
            sb.append(count.getTime());
        }

        final JspWriter writer = getJspContext().getOut();
        writer.print(sb.toString());
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getMetric() {
        return metric;
    }

    public void setMetric(String metric) {
        this.metric = metric;
    }
}
