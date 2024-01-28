package com.cy.store.service.ex;

/*
    正在执行数据插入操作的时候
    ,服务器宕机或数据库宕机.这种情况是处于正在执行插入的过程中所产生的异常,
    起名InsertException异常
 */
public class InsertException extends ServiceException{
    public InsertException() {
        super();
    }

    public InsertException(String message) {
        super(message);
    }

    public InsertException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsertException(Throwable cause) {
        super(cause);
    }

    protected InsertException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
