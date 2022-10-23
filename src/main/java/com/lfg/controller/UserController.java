package com.lfg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lfg.common.R;
import com.lfg.entity.User;
import com.lfg.service.impl.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户管理
 */
@Api(value = "/user", tags = {"用户管理"})
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "body", dataType = "Map", name = "map", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "HttpSession", name = "session", value = "", required = true)
    })
    @ApiOperation(value = "用户登陆", notes = "", httpMethod = "POST")
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取邮箱
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //session
        Object codeSession = session.getAttribute(phone);
        //进行验证码比对
        if(codeSession != null &&codeSession.equals(code)){
            //成功说明登陆成功
            LambdaQueryWrapper<User> queryWrapper =new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            //判断手机号是否为新用户，如果是新用户 自动完成注册
            if (user == null){
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                user.setName("新用户_"+code);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);


        }


        return R.error("短信验证失败");
    }
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        session.invalidate();
        return R.success("退出成功！");
    }
}
