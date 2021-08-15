package com.hansoleee.corespringsecurity.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hansoleee.corespringsecurity.domain.AccountDTO;
import com.hansoleee.corespringsecurity.security.token.AjaxAuthenticationToken;
import org.modelmapper.internal.util.Assert;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.springframework.util.ObjectUtils.isEmpty;

public class AjaxLoginProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper = new ObjectMapper();

    public AjaxLoginProcessingFilter() {
        super(new AntPathRequestMatcher("/api/login"));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        Assert.isTrue(isAjax(request), "Authentication is not supported");

        AccountDTO accountDTO = objectMapper.readValue(request.getReader(), AccountDTO.class);
        if (isEmpty(accountDTO.getUsername()) || isEmpty(accountDTO.getPassword())) {
            throw new IllegalArgumentException("Username or Password is Empty");
        }

        AjaxAuthenticationToken ajaxAuthenticationToken = new AjaxAuthenticationToken(accountDTO.getUsername(), accountDTO.getPassword());

        return getAuthenticationManager().authenticate(ajaxAuthenticationToken);
    }

    private boolean isAjax(HttpServletRequest request) {
        return request.getHeader("X-Requested-With").equals("XMLHttpRequest");
    }
}
