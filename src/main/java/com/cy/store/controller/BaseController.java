package com.cy.store.controller;

import com.cy.store.service.ex.InsertException;
import com.cy.store.service.ex.ServiceException;
import com.cy.store.service.ex.UsernameDuplicatedException;
import com.cy.store.util.JsonResult;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpSession;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.attribute.UserPrincipalNotFoundException;

/**
 * 控制层里面类的基类
 */
public class BaseController {

    //操作成功的状态码
    public static final int OK = 200;

    /**
     * 1.@ExceptionHandler表示该方法用于处理捕获抛出的异常
     * 2.什么样的异常才会被这个方法处理呢?所以需要ServiceException.class,这样
     * 的话只要是抛出ServiceException异常就会被拦截到handleException方法,此
     * 时handleException方法就是请求处理方法,返回值就是需要传递给前端的数据
     * 3.被ExceptionHandler修饰后如果项目发生异常,那么异常对象就会被自动传递
     * 给此方法的参数列表上,所以形参就需要写Throwable e用来接收异常对象
     */
    @ExceptionHandler({ServiceException.class, FileAlreadyExistsException.class})
    public JsonResult<Void> handleException(Throwable e) {
        JsonResult<Void> result = new JsonResult<>(e);
        if (e instanceof UsernameDuplicatedException) {
            result.setState(4000);
            result.setMessage("用户名已经被占用的异常");
        }else if (e instanceof InsertException) {
            result.setState(5000);
            result.setMessage("注册时产生的未知的异常");
        }
        return result;
    }

    /**
     * 获取session对象中uid
     * @param session session 对象
     * @return 当前登录用户uid的值
     */
    protected final Integer getuidFormSession(HttpSession session) {
        return Integer.valueOf(session.getAttribute("uid").toString());
    }

    /**
     * 获取session对象中username
     * @param session session 对象
     * @return 当前登录用户username的值
     */
    protected final String getUsernameFromSesssion(HttpSession session) {
        return session.getAttribute("username").toString();
    }
}
