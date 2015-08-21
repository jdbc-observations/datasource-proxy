package net.ttddyy.dsproxy.listener;

import net.ttddyy.dsproxy.QueryType;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryUtils {
    public static String removeCommentAndWhiteSpace(String query) {
        if (query == null) {
            return null;
        }
        return query.replaceAll("--.*\n", "").replaceAll("\n", "").replaceAll("/\\*.*\\*/", "").trim();
    }

    /**
     * Returns type of query from given query string.
     *
     * @param query a query string
     * @return type of query
     * @since 1.4
     */
    public static QueryType getQueryType(String query) {

        final String trimmedQuery = removeCommentAndWhiteSpace(query);
        if (trimmedQuery == null || trimmedQuery.length() < 1) {
            return QueryType.OTHER;
        }

        final char firstChar = trimmedQuery.charAt(0);
        final QueryType type;
        switch (firstChar) {
            case 'S':
            case 's':
                type = QueryType.SELECT;
                break;
            case 'I':
            case 'i':
                type = QueryType.INSERT;
                break;
            case 'U':
            case 'u':
                type = QueryType.UPDATE;
                break;
            case 'D':
            case 'd':
                type = QueryType.DELETE;
                break;
            default:
                type = QueryType.OTHER;
        }
        return type;
    }

}
