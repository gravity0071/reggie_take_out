package com.raggie.filter;


import com.alibaba.fastjson.JSON;
import com.raggie.common.BaseContext;
import com.raggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //match path, supports /**
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//        log.info("get the request: {}", request.getRequestURI());

        String requstURI = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/employee/page",
                "/backend/**",
                "/front/**",
                "/commen/**",
                "/user/sendMsg",
                "/user/login"
        };

        String[] back = new String[]{

        };

        boolean check = check(requstURI, urls);
        if(check){
            log.info("don't need intercept" + requstURI);
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        else if(request.getSession().getAttribute("employee") != null){
            log.info("already login " + requstURI);
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        if(request.getSession().getAttribute("user") != null){
            log.info("already login " + requstURI);
            BaseContext.setCurrentIdUser((Long)request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }
        else{
            log.info("not log in: {}",requstURI);
            //response to client, since request.js has method to determine whether a user have logged
            //if not log in, the browser will redirect to login page
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return;
        }
    }

    public boolean check(String requestURI, String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if(match) return true;
        }
        return false;
    }
}
