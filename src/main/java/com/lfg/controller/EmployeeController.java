package com.lfg.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lfg.common.R;
import com.lfg.entity.Employee;
import com.lfg.service.impl.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 员工管理
 */
@Api(value = "/employee", tags = {"员工管理"})
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆
     *
     * @param request  获取session
     * @param employee 员工类
     * @return 返回自定义r类
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "HttpServletRequest", name = "request", value = "获取session", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "Employee", name = "employee", value = "员工类", required = true)
    })
    @ApiOperation(value = "员工登陆", notes = "员工登陆", httpMethod = "POST")
    @PostMapping("login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1、将页面提交的密码进行md5处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee one = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登陆失败结果

        if (one == null) {
            return R.error("用户名不存在");
        }
        //4、密码比对，如果不一致返回登陆失败
        if (!one.getPassword().equals(password)) {
            return R.error("密码错误");
        }

        //5、查看员工状态，如果被禁用返回员工已禁用结果
        if (one.getStatus() == 0) {
            return R.error("账号被禁用");
        }
        //6、登陆成功，讲员工id存入session  并返回登陆成功
        request.getSession().setAttribute("employee", one.getId());
        return R.success(one);
    }

    /**
     * 退出
     *
     * @param清除session
     * @return 返回r类 信息
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "HttpServletRequest", name = "request", value = "", required = true)
    })
    @ApiOperation(value = "退出", notes = "退出", httpMethod = "POST")
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */

    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "HttpServletRequest", name = "request", value = "request", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "Employee", name = "employee", value = "employee", required = true)
    })
    @ApiOperation(value = "新增员工", notes = "新增员工", httpMethod = "POST")
    @PostMapping()
    public R<String> save( HttpServletRequest request, @RequestBody Employee employee) {
        //设置出事密码123456，需要进行md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获得当前登陆用户id
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 员工信息查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "string", name = "name", value = "", required = true)
    })
    @ApiOperation(value = "员工信息查询", notes = "员工信息查询", httpMethod = "GET")
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);


        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Employee::getName, name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo, lambdaQueryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 根据id修改员工信息
     *
     * @param employee
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "HttpServletRequest", name = "request", value = "", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "Employee", name = "employee", value = "", required = true)
    })
    @ApiOperation(value = "根据id修改员工信息", notes = "根据id修改员工信息", httpMethod = "PUT")
    @PutMapping
    public R<String> updata(HttpServletRequest request, @RequestBody Employee employee) {
        //获取season中到用户id
        Long empId = (Long) request.getSession().getAttribute("employee");
        //设置employee更新时间
//        employee.setUpdateTime(LocalDateTime.now());
        //设置employee更新用户
//        employee.setUpdateUser(empId);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 按id查询员工信息
     * @param id
     * @return
     */
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "long", name = "id", value = "", required = true)
    })
    @ApiOperation(value = "按id查询员工信息", notes = "按id查询员工信息", httpMethod = "GET")
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee byId = employeeService.getById(id);
        if(byId != null){
            return R.success(byId);
        }else {
            return R.error("没有查询到");
        }

    }
}
