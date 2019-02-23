package com.anluy.admin.utils;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2019/2/18.
 */
public class Neo4jException extends RuntimeException {
    public Neo4jException() {
    }

    public Neo4jException(String message) {
        super(message);
    }

    public Neo4jException(String message, Throwable cause) {
        super(message, cause);
    }

    public Neo4jException(Throwable cause) {
        super(cause);
    }

    public Neo4jException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
