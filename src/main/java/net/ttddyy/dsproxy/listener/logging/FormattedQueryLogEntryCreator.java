package net.ttddyy.dsproxy.listener.logging;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.core.FormatConfig;

/**
 * @author Raphael Vullriede
 */
public class FormattedQueryLogEntryCreator extends DefaultQueryLogEntryCreator {


    private FormatConfig formatConfig;


    public FormattedQueryLogEntryCreator() {
        // TODO expose available config options?
        this.formatConfig = FormatConfig.builder().build();
    }


    @Override
    protected String formatQuery(String query) {
        return SqlFormatter.format(query, this.formatConfig);
    }
}