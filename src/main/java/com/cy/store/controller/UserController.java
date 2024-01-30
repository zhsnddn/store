package com.cy.store.controller;

import com.cy.store.controller.ex.*;
import com.cy.store.entity.User;
import com.cy.store.service.IUserService;
import com.cy.store.util.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController //其作用等同于@Controller+@ResponseBody
//@Controller
@RequestMapping("users")
public class UserController extends BaseController {

    @Autowired
    private IUserService userService;

    @RequestMapping("reg")
    //@ResponseBody //表示此方法的响应结果以json格式进行数据的响应给到前端
    public JsonResult<Void> reg(User user) {
        userService.reg(user);
        return new JsonResult<>(OK);
    }

    @RequestMapping("login")
    public JsonResult<User> login(String username, String password, HttpSession session) {
        User data = userService.login(username, password);

        //向session对象中完成数据的绑定(这个session是全局的,项目的任何位置都可以访问)
        session.setAttribute("uid",data.getUid());
        session.setAttribute("username",data.getUsername());

        //测试能否正常获取session中存储的数据
        System.out.println(getuidFormSession(session));
        System.out.println(getUsernameFromSesssion(session));

        return new JsonResult<User>(OK,data);
    }

    @RequestMapping("change_password")
    public JsonResult<Void> changePassword(String oldPassword,
                                           String newPassword,
                                           HttpSession session) {
        Integer uid = getuidFormSession(session);
        String username = getUsernameFromSesssion(session);
        userService.changePassword(uid,username,oldPassword,newPassword);
        return new JsonResult<>(OK);
    }

    @RequestMapping("get_by_uid")
    public JsonResult<User> getByUid(HttpSession session) {
        User data = userService.getByUid(getuidFormSession(session));
        return new JsonResult<User>(OK,data);
    }

    @RequestMapping("change_info")
    public JsonResult<Void> changeInfo(User user,HttpSession session) {
        //user对象中有四部分的数据:username,phone,email,gender
        //控制层给业务层传递uid,并在业务层通过user.setUid(uid);将uid封装到user中
        Integer uid = getuidFormSession(session);
        String username = getUsernameFromSesssion(session);
        userService.changeInfo(uid, username, user);
        return new JsonResult<>(OK);
    }

    //文件上传文件最大值
    /**
     * MultipartFile接口是springmvc提供的一个接口,这个接口为
     * 我们包装了获取文件类型的数据(任何类型的file都可以接收),
     * springboot又整合了springmvc,只需要在处理请求的方法参数
     * 列表上声明一个类型为MultipartFile的参数,然后springboot
     * 自动将传递给服务器的文件数据赋值给这个参数
     */
    //设置上传文件的最大值
    public static final int AVATAR_MAX_SIZE = 10*1024*1024;

    //限制上传文件的类型
    public static final List<String> AVATAR_TYPE = new ArrayList<>();
    //给集合初始化:
    static {
        AVATAR_TYPE.add("image/jpeg");
        AVATAR_TYPE.add("image/png");
        AVATAR_TYPE.add("image/bmp");
        AVATAR_TYPE.add("image/gif");
//      AVATAR_TYPE.add("image/jpg");//为啥不需要这个呢
    }
    @RequestMapping("change_avatar")
    public JsonResult<String> changeAvatar(HttpSession session,
                                           MultipartFile file) {
        /**
         * 1.参数名为什么必须用file:在upload.html页面的147行<input type=
         * "file" name="file">中的name="file",所以必须有一个方法的参数名
         * 为file用于接收前端传递的该文件.如果想要参数名和前端的name不一
         * 样:@RequestParam("file")MultipartFile ffff:把表单中name=
         * "file"的控件值传递到变量ffff上
         * 2.参数类型为什么必须是MultipartFile:这是springmvc中封装的一个
         * 包装接口,如果类型是MultipartFile并且参数名和前端上传文件的name
         * 相同,则会自动把整体的数据包传递给file
         */
        //判断文件是否为null
        if (file.isEmpty()) {
            throw new FileEmptyException("文件为空");
        }
        if (file.getSize()>AVATAR_MAX_SIZE) {
            throw new FileSizeException("文件超出限制");
        }
        //判断文件的类型是否是我们规定的后缀类型
        String contentType = file.getContentType();
        //如果集合包含某个元素则返回值为true
        if (!AVATAR_TYPE.contains(contentType)) {
            throw new FileTypeException("文件类型不支持");
        }

        //上传的文件路径:.../upload/文件名.png
        /**
         * session.getServletContext()获取当前Web应用程序的上下文
         * 对象(每次启动tomcat都会创建一个新的上下文对象)
         * getRealPath("/upload")的/代表当前web应用程序的根目录,通过该相
         * 对路径获取绝对路径,返回一个路径字符串,如果不能进行映射返回null,单
         * 斜杠可要可不要
         */
        String parent =
                session.getServletContext().getRealPath("/upload");
        System.out.println(parent);//调试用

        //File对象指向这个路径,通过判断File是否存在得到该路径是否存在
        File dir = new File(parent);
        if (!dir.exists()) {//检测目录是否存在
            dir.mkdirs();//创建当前目录
        }

        //获取这个文件名称(文件名+后缀,如avatar01.png,不包含父目录结构)用UUID
        // 工具生成一个新的字符串作为文件名(好处:避免了因文件名重复发生的覆盖)
        String originalFilename = file.getOriginalFilename();
        System.out.println("OriginalFilename="+originalFilename);
        int index = originalFilename.lastIndexOf(".");
        String suffix = originalFilename.substring(index);
        //filename形如SAFS1-56JHIOHI-HIUGHUI-5565TYRF.png
        String filename =
                UUID.randomUUID().toString().toUpperCase()+suffix;

        //在dir目录下创建filename文件(此时是空文件)
        File dest = new File(dir, filename);

        //java可以把一个文件的数据直接写到同类型的文件中,这里将参数file中的数据写入到空文件dest中
        try {
            file.transferTo(dest);//transferTo是一个封装的方法,用来将file文件中的数据写入到dest文件

            /**
             * 先捕获FileStateException再捕获IOException是
             * 因为后者包含前者,如果先捕获IOException那么
             * FileStateException就永远不可能会被捕获
             */
        } catch (FileStateException e) {
            throw new FileStateException("文件状态异常");
        } catch (IOException e) {
            //这里不用打印e,而是用自己写的FileUploadIOException类并
            // 抛出文件读写异常
            throw new FileUploadIOException("文件读写异常");
        }

        Integer uid = getuidFormSession(session);
        String username = getUsernameFromSesssion(session);
        String avatar = "/upload/" + filename;
        userService.changeAvatar(uid,avatar, username);
        //返回用户头像的路径给前端页面,将来用于头像展示使用
        return new JsonResult<>(OK, avatar);
    }



}
