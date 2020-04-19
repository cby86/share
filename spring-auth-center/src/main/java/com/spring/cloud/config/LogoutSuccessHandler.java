package com.spring.cloud.config;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Component
public class LogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (isAjax(request)) {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            HashMap hashMap = new HashMap();
            hashMap.put("status", 1);
            hashMap.put("msg", "成功");
            PrintWriter writer = response.getWriter();
            writer.print(JSONObject.toJSONString(hashMap));
            writer.flush();
            writer.close();
        } else {
            super.onLogoutSuccess(request, response, authentication);
        }
    }

    public boolean isAjax(HttpServletRequest request) {
        String requestType = request.getHeader("X-Requested-With");
        return StringUtils.hasText(requestType) && requestType.equals("XMLHttpRequest") || StringUtils.hasText(request.getHeader(HttpHeaders.ORIGIN));
    }

}
