package com.lfg.filter;
import com.alibaba.fastjson.JSON;
import com.lfg.common.BaseContext;
import com.lfg.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public  class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1、获取本次请求的uri
        String requestURI = request.getRequestURI();
        //定义不需要处理的请求路径
        String[] urls =new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources",
                "/v2/api-docs",
                "/send-mail/simple",
                "/user/login"
        };
        //2、判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3、如果不需要处理直接放行
        if(check){
            filterChain.doFilter(request,response);
            return;
        }
        //4-1、判断登陆状态，如果已登陆，则直接放行
        if(request.getSession().getAttribute("employee" )!= null){

            Long employee = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employee);


            filterChain.doFilter(request,response);
            return;
        }
        //4-2、判断移动端登陆状态，如果已登陆，则直接放行
        if(request.getSession().getAttribute("user" )!= null){

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);


            filterChain.doFilter(request,response);
            return;
        }
        //5、如果未登陆则返回未登陆结果
        log.info(requestURI);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


    }
    public boolean check(String[] urls , String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
