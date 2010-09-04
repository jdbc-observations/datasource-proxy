package net.ttddyy.dsproxy.proxy;

import java.util.List;

/**
 * @author Tadaya Tsuyukubo
 */
public class BatchQueryHolder {
    private String query;
    private List<?> args;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<?> getArgs() {
        return args;
    }

    public void setArgs(List<?> args) {
        this.args = args;
    }
}
