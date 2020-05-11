package com.spring.cloud.config;

import com.alibaba.fastjson.JSONObject;
import com.spring.cloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Autowired
    CustomerAuthenticationEntryPoint customerAuthenticationEntryPoint;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        auth.authenticationProvider(daoAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.csrf().disable().headers().frameOptions().disable();
        http.formLogin().loginPage("/loginOut")
                .failureHandler((request,response,exception)->{
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    HashMap hashMap = new HashMap();
                    hashMap.put("status", 0);
                    hashMap.put("message", "用户名或密码错误");
                    PrintWriter writer = response.getWriter();
                    writer.print(JSONObject.toJSONString(hashMap));
                    writer.flush();
                    writer.close();
                })
                .loginProcessingUrl("/dologin").successHandler(loginSuccessHandler)
                .permitAll();
        http.logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler);
        http.authorizeRequests()
                .antMatchers("/user/**").hasRole("SUPER_ADMIN")
                .antMatchers("/actuator/**").permitAll()
                .anyRequest()
                .authenticated().and()
                .exceptionHandling().authenticationEntryPoint(customerAuthenticationEntryPoint);
    }
}

