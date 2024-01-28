package com.cy.store.service.ex;

/**
 * 因为整个业务的异常只有一种情况下才会产生:只有运行时才会产生,不运行不会产生
 * 所以要求业务层的异常都要继承运行时异常RuntimeException并且重写父类的所有构造方法以便后期能抛出自已定义的异常
 */
public class ServiceException extends RuntimeException{
    //什么也不返回
    public ServiceException() {
        super();
    }

    //返回异常信息(常用)
    public ServiceException(String message) {
        super(message);
    }

    //返回异常信息和异常对象(常用)
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    protected ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
