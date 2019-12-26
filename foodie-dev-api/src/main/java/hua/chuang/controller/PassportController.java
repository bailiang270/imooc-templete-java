package hua.chuang.controller;


import hua.chuang.pojo.Users;
import hua.chuang.pojo.bo.UserBO;
import hua.chuang.service.StuService;
import hua.chuang.service.UserService;
import hua.chuang.utils.CookieUtils;
import hua.chuang.utils.IMOOCJSONResult;
import hua.chuang.utils.JsonUtils;
import hua.chuang.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Api(value = "注册登录",tags = {"用于注册登录的相关接口"})
@RestController
@RequestMapping("passport")
public class PassportController {




    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户名是否存在" , notes = "用户名是否存在", httpMethod = "GET")
    @GetMapping("/usernameIsExist")
    public IMOOCJSONResult usernameIsExist(@RequestParam String username){

        //1.判断用户名是否为空
        if (StringUtils.isBlank(username)){
            return IMOOCJSONResult.errorMsg("用户名为空");
        }
        //2.判断用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }
        //请求成功，用户名校验成功
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户注册" , notes = "用户注册", httpMethod = "POST")
    @PostMapping("/regist")
    public IMOOCJSONResult regist(@RequestBody UserBO userBO,
                                  HttpServletRequest request,
                                  HttpServletResponse response){

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        String confirmPassword = userBO.getConfirmPassword();

        // 0.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username)||
                StringUtils.isBlank(password)||
                StringUtils.isBlank(confirmPassword)){
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
        // 1.查询用户名是否存在
        boolean isExist = userService.queryUsernameIsExist(username);
        if (isExist){
            return IMOOCJSONResult.errorMsg("用户名已存在");
        }
        // 2.密码长度不少于6位
        if (password.length()<6){
            return IMOOCJSONResult.errorMsg("密码长度不能小于6");
        }
        // 3.判断两次密码是否一致
        if (!password.equals(confirmPassword)){
            return IMOOCJSONResult.errorMsg("两次密码输入不一致");
        }
        // 4.实现注册
        Users resultUsers = userService.createUser(userBO);

        resultUsers = setNullProperty(resultUsers);

        //添加cookie
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(resultUsers),true);

        //请求成功，用户名校验成功
        return IMOOCJSONResult.ok();
    }

    @ApiOperation(value = "用户登录" , notes = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public IMOOCJSONResult login(@RequestBody UserBO userBO,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {

        String username = userBO.getUsername();
        String password = userBO.getPassword();
        

        // 0.判断用户名和密码必须不为空
        if (StringUtils.isBlank(username)||
                StringUtils.isBlank(password)){
            return IMOOCJSONResult.errorMsg("用户名或者密码不能为空");
        }
       
        // 1.实现登录
        Users resultUsers = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

        if (resultUsers==null){
            return IMOOCJSONResult.errorMsg("用户名或密码错误");
        }

        resultUsers = setNullProperty(resultUsers);

        //添加cookie
        CookieUtils.setCookie(request,response,"user", JsonUtils.objectToJson(resultUsers),true);

        //请求成功，用户名校验成功
        return IMOOCJSONResult.ok(resultUsers);
    }


    private Users setNullProperty(Users resultUsers){
        resultUsers.setMobile(null);
        resultUsers.setUpdatedTime(null);
        resultUsers.setCreatedTime(null);
        resultUsers.setPassword(null);
        resultUsers.setEmail(null);
        resultUsers.setBirthday(null);
        return resultUsers;
    }

    @ApiOperation(value = "用户退出登录" , notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("/logout")
    public IMOOCJSONResult logout(@RequestParam String userId,
                                  HttpServletRequest request,
                                  HttpServletResponse response){

        //清除用户的相关系信息
        CookieUtils.deleteCookie(request,response,"user");

        //todo 用户退出登录，需要清空购物车
        //todo 分布式会话，需要清除用户数据

        return IMOOCJSONResult.ok();
    }


}
