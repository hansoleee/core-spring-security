package com.hansoleee.corespringsecurity.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansoleee.corespringsecurity.security.filter.AjaxLoginProcessingFilter;
import com.hansoleee.corespringsecurity.security.handler.AjaxAuthenticationFailureHandler;
import com.hansoleee.corespringsecurity.security.handler.AjaxAuthenticationSuccessHandler;
import com.hansoleee.corespringsecurity.security.handler.FormAuthenticationFailureHandler;
import com.hansoleee.corespringsecurity.security.handler.FormAuthenticationSuccessHandler;
import com.hansoleee.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@EnableWebSecurity
//@RequiredArgsConstructor
@Order(0)
public class AjaxSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AjaxAuthenticationProvider ajaxAuthenticationProvider;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AuthenticationFailureHandler authenticationFailureHandler;

    public AjaxSecurityConfig(AjaxAuthenticationProvider ajaxAuthenticationProvider,
                              @Qualifier("ajaxAuthenticationSuccessHandler") AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler,
                              @Qualifier("ajaxAuthenticationFailureHandler") AuthenticationFailureHandler ajaxAuthenticationFailureHandler) {
        this.ajaxAuthenticationProvider = ajaxAuthenticationProvider;
        this.authenticationSuccessHandler = ajaxAuthenticationSuccessHandler;
        this.authenticationFailureHandler = ajaxAuthenticationFailureHandler;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ajaxAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .addFilterBefore(ajaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter.class)

                .antMatcher("/api/**")
                .authorizeRequests()
                .anyRequest().authenticated();
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AjaxLoginProcessingFilter ajaxLoginProcessingFilter() throws Exception {
        AjaxLoginProcessingFilter ajaxLoginProcessingFilter = new AjaxLoginProcessingFilter();
        ajaxLoginProcessingFilter.setAuthenticationManager(authenticationManagerBean());
        ajaxLoginProcessingFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        ajaxLoginProcessingFilter.setAuthenticationFailureHandler(authenticationFailureHandler);
        return ajaxLoginProcessingFilter;
    }
}
