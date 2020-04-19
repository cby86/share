package com.spring.cloud.config;

import com.alibaba.fastjson.JSONObject;
import com.spring.cloud.entity.User;
import com.spring.cloud.utils.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by chenbangyuan on 2015/11/9.
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public LoginSuccessHandler() {
        setDefaultTargetUrl("/index");
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (request.getParameter("targetUrl")!=null) {
            response.sendRedirect(request.getParameter("targetUrl"));
            return;
        }
        if (isAjax(request)) {
            JSONObject hashMap = new JSONObject();
            hashMap.put("status", "1");
            hashMap.put("msg", "成功");
            HttpSession session = request.getSession(true);
            JSONObject data = (JSONObject) JSONObject.toJSON(authentication.getPrincipal());
            data.put("token", session.getId());
            User user = SecurityUtils.currentUser();
            data.put("mobile", user.getUsername());
            data.put("username", user.getUsername());
            data.put("authorities", user.getAuthorities());
            data.put("times", user.getTimes());
            data.put("realName", user.getRealName());
            hashMap.put("data",data);
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.getWriter().print(hashMap.toString());
            response.getWriter().flush();
            response.getWriter().close();
        }else {
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    public boolean isAjax(HttpServletRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        return StringUtils.hasText(requestType) && requestType.equals("XMLHttpRequest") || StringUtils.hasText(request.getHeader(HttpHeaders.ORIGIN));
    }

}
