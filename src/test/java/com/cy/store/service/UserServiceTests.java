package com.cy.store.service;

import com.cy.store.entity.User;
import com.cy.store.service.ex.ServiceException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserServiceTests {

    @Autowired
    private IUserService userService;

    @Test
    public void reg() {
        /**
         * 进行插入时可能会出错抛出异常,这时需要捕获异常:
         * 1.选中    User user = new User();
         *           user.setUsername("张7");
         *           user.setPassword("123456");
         *           userService.reg(user);
         *           System.out.println("OK");
         * 2.点击导航栏的Code,然后依次点击SurroundWith->try/catch就可以捕获异常了
         * 3.Exception e没有问题,但这里我们知道是Service层的异常,所以可以改为ServiceException e
         * 4.System.out.println(e.getClass().getSimpleName());获取异常对象再获取类的名称然后输出
         * 5.System.out.println(e.getMessage());输出异常信息(是自己在ServiceException的子类类具体设置的信息)
         */
        try {
            User user = new User();
            user.setUsername("yyyy");
            user.setPassword("123");
            userService.reg(user);
            System.out.println("OK");
        } catch (ServiceException e) {
            System.out.println(e.getClass().getSimpleName());
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void login() {
        User user = userService.login("zym", "123");
        System.out.println(user);
    }
}
