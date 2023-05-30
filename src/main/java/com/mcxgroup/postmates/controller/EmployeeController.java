package com.mcxgroup.postmates.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import com.mcxgroup.postmates.entity.Employee;
import com.mcxgroup.postmates.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @Description: 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private CacheManager cacheManager;
    /**
     * @param employee 登录信息封装对象
     * @Description: 员工登录
     *
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用");
        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee", emp.getId());
        log.info(emp.getName()+"已经登录系统,ID为{}",emp.getId());
        BaseContext.setCurrentEmpId(emp.getId());//存入ThreadLocal
        return R.success(emp);
    }

    /**
     * @param request http请求
     * @Description: 员工退出
     *
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        request.getSession().removeAttribute("employee");
//        BaseContext.setCurrentEmpId((long) -1);
        return R.success("退出成功");
    }

    /**
     * @param employee 将请求内容封装为Employee对象接收
     * @Description: 新增员工
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工....{}", employee);

        // 为员工设置默认的登录密码，为123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        // 设置创建和登录时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        // 获取当前登录用户信息
        // Session中存储的是登录用户的id信息
        long curUserId = (long) request.getSession().getAttribute("employee");

        // 设置创建人和修改人的id
        employee.setCreateUser(curUserId);
        employee.setUpdateUser(curUserId);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * @Description: 查询员工信息
     * @param page  页码
     * @param pageSize 页面尺寸
     * @param name 员工姓名
     */
    @GetMapping("/page")
    public R<Page<Employee>> page(int page, int pageSize, String name) {
        log.info(page + ":" + pageSize + ":" + name);

        // 构造分页构造器
        Page<Employee> pageInfo = new Page<>(page,pageSize);

        // 构造条件过滤器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();

        // 添加查询条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        // 添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * @Description: 根据id修改员工信息
     * @param employee 封装对象信息,前端仅有id和status正确传送
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
        log.info("修改了信息：{}",employee.toString());
        Long empId = (Long) request.getSession().getAttribute("employee");
        if (employee.getPhone()==null){
            System.out.println("employee = " + employee);
            Employee empbyId = employeeService.getById(employee.getId());
            empbyId.setUpdateTime(LocalDateTime.now());
            empbyId.setUpdateUser(empId);
            empbyId.setStatus(employee.getStatus());
            empbyId.setName(employee.getName());
            empbyId.setUsername(employee.getUsername());
            employeeService.updateById(empbyId);
            return R.success("员工状态修改成功");
        } else {
            employee.setUpdateTime(LocalDateTime.now());
            employee.setUpdateUser(empId);
            employeeService.updateById(employee);
            return R.success("员工信息修改成功");
        }
//        return R.success("员工信息修改成功");
    }
    /**
     * @Description: 根据id查员工信息
     * @param id 用户id
     * @Author: MCXEN
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null) {
            return R.success(employee);
        }
        return R.error("没有该用户！");
    }
}
