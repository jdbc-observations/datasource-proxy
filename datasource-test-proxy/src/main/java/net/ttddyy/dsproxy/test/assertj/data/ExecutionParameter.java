package net.ttddyy.dsproxy.test.assertj.data;

import java.sql.SQLType;

/**
 * @author Tadaya Tsuyukubo
 * @since 1.4
 */
public abstract class ExecutionParameter {

    public static abstract class ExecutionParameterByIndex extends ExecutionParameter {
        protected int paramIndex;

        public int getParamIndex() {
            return paramIndex;
        }
    }

    public static abstract class ExecutionParameterByName extends ExecutionParameter {
        protected String paramName;

        public String getParamName() {
            return paramName;
        }
    }


    public static class ParamExecutionByIndex extends ExecutionParameterByIndex {
        private Object value;

        public ParamExecutionByIndex(int paramIndex, Object value) {
            this.paramIndex = paramIndex;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }

    public static class ParamExecutionByName extends ExecutionParameterByName {
        private Object value;

        public ParamExecutionByName(String paramName, Object value) {
            this.paramName = paramName;
            this.value = value;
        }

        public Object getValue() {
            return value;
        }
    }


    public static class NullParamExecutionByIndex extends ExecutionParameterByIndex {
        private Integer sqlType;  // null if do not check sqlType

        public NullParamExecutionByIndex(int paramIndex, Integer sqlType) {
            this.paramIndex = paramIndex;
            this.sqlType = sqlType;
        }

        public Integer getSqlType() {
            return sqlType;
        }
    }

    public static class NullParamExecutionByName extends ExecutionParameterByName {
        private Integer sqlType;

        public NullParamExecutionByName(String paramName, Integer sqlType) {
            this.paramName = paramName;
            this.sqlType = sqlType;
        }

        public Integer getSqlType() {
            return sqlType;
        }
    }

    public static class OutParamExecutionByIndexWithIntType extends ExecutionParameterByIndex {
        private int sqlType;

        public OutParamExecutionByIndexWithIntType(int paramIndex, int sqlType) {
            this.paramIndex = paramIndex;
            this.sqlType = sqlType;
        }

        public int getSqlType() {
            return sqlType;
        }
    }

    public static class OutParamExecutionByNameWithIntType extends ExecutionParameterByName {
        private int sqlType;

        public OutParamExecutionByNameWithIntType(String paramName, int sqlType) {
            this.paramName = paramName;
            this.sqlType = sqlType;
        }

        public int getSqlType() {
            return sqlType;
        }
    }

    public static class OutParamExecutionByIndexWithSQLType extends ExecutionParameterByIndex {
        private SQLType sqlType;

        public OutParamExecutionByIndexWithSQLType(int paramIndex, SQLType sqlType) {
            this.paramIndex = paramIndex;
            this.sqlType = sqlType;
        }

        public SQLType getSqlType() {
            return sqlType;
        }
    }

    public static class OutParamExecutionByNameWithSQLType extends ExecutionParameterByName {
        private SQLType sqlType;

        public OutParamExecutionByNameWithSQLType(String paramName, SQLType sqlType) {
            this.paramName = paramName;
            this.sqlType = sqlType;
        }

        public SQLType getSqlType() {
            return sqlType;
        }
    }


    public static ExecutionParameter param(int paramIndex, Object value) {
        return new ParamExecutionByIndex(paramIndex, value);
    }

    public static ExecutionParameter param(String paramName, Object value) {
        return new ParamExecutionByName(paramName, value);
    }

    public static ExecutionParameter nullParam(int index, int sqlType) {
        return new NullParamExecutionByIndex(index, sqlType);
    }

    // do not care sqlType
    public static ExecutionParameter nullParam(int index) {
        return new NullParamExecutionByIndex(index, null);
    }

    public static ExecutionParameter nullParam(String name, int sqlType) {
        return new NullParamExecutionByName(name, sqlType);
    }

    public static ExecutionParameter nullParam(String name) {
        return new NullParamExecutionByName(name, null);
    }

    public static ExecutionParameter outParam(int paramIndex, int sqlType) {
        return new OutParamExecutionByIndexWithIntType(paramIndex, sqlType);
    }

    public static ExecutionParameter outParam(int paramIndex, SQLType sqlType) {
        return new OutParamExecutionByIndexWithSQLType(paramIndex, sqlType);
    }

    public static ExecutionParameter outParam(String paramName, int sqlType) {
        return new OutParamExecutionByNameWithIntType(paramName, sqlType);
    }

    public static ExecutionParameter outParam(String paramName, SQLType sqlType) {
        return new OutParamExecutionByNameWithSQLType(paramName, sqlType);
    }

}
