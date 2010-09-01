package com.tpon.dsproxy.listener;

/**
 * @author Tadaya Tsuyukubo
 */
public class QueryUtils {
    public static String removeCommentAndWhiteSpace(String query) {
        return query.replaceAll("--.*\n", "").replaceAll("\n", "").replaceAll("/\\*.*\\*/", "").trim();
    }
    
}
