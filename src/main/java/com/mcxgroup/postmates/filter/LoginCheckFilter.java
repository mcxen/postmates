package com.mcxgroup.postmates.filter;

import com.alibaba.fastjson.JSON;
//import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.BaseContext;
import com.mcxgroup.postmates.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 用户登录过滤器
 * @author: MCXEN
 * @date: 2022/11/26
 * MCXEN
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    // 路径匹配工具对象
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    /**
     * @param servletRequest servlet请求
     * @param servletResponse servlet响应
     * @param filterChain 过滤器
     * @Description: 登录拦截过滤
     * @Author:  <a href="https://www.MCXEN.com/">MCXEN</a>
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String requestURI = request.getRequestURI();
        log.info("用户登录过滤器成功拦截到请求：" + requestURI);

        //1.不用拦截的请求uri
        String[] urls = {
                "/backend/**",      // 后台静态资源
                "/front/**",        // 前台静态资源
                "/employee/login",  // 员工登录
                "/employee/logout", // 员工注册
                "/user/login",      // 用户登录
                "/user/sendMsg",    // 发送验证码
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs"
        };

        // 匹配成功,直接放行
        if (check(urls, requestURI)) {
            log.info("匹配成功，已放行....");
            filterChain.doFilter(request, response);
            return;
        }

        //2.1员工已登录
        Object employeeId = request.getSession().getAttribute("employee");
        if (employeeId != null) {
            log.info("员工已登录，已放行....");

//             将当前登录的员工id存入ThreadLocal
            BaseContext.setCurrentEmpId((Long) employeeId);

            filterChain.doFilter(request,response);
            return;
        }

        //2.2用户已登录
        Object userId = request.getSession().getAttribute("user");
        if (userId != null) {
            log.info("用户已登录，已放行....");

//             将当前登录的用户id存入ThreadLocal
            BaseContext.setCurrentId((Long) userId);

            filterChain.doFilter(request,response);
            return;
        }

        //3.均未登录
        log.info("未登录，已拦截....");
        response.getWriter().write(JSON.toJSONString(R.error("NOLOGIN")));
        response.flushBuffer();
    }

    /**
     * @param urls       放行的url
     * @param requestUri 请求的url
     * @Description: 检查路径是否匹配，以获得放行
     */
    public boolean check(String[] urls, String requestUri) {
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, requestUri);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
