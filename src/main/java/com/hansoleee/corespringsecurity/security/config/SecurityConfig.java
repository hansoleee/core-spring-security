package com.hansoleee.corespringsecurity.security.config;

import com.hansoleee.corespringsecurity.security.common.FormWebAuthenticationDetailsSource;
import com.hansoleee.corespringsecurity.security.filter.PermitAllFilter;
import com.hansoleee.corespringsecurity.security.handler.FormAccessDeniedHandler;
import com.hansoleee.corespringsecurity.security.provider.AjaxAuthenticationProvider;
import com.hansoleee.corespringsecurity.security.provider.FormAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.util.List;

@Slf4j
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final FormWebAuthenticationDetailsSource formWebAuthenticationDetailsSource;
    private final AuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler ajaxAuthenticationFailureHandler;
    private final AuthenticationSuccessHandler formAuthenticationSuccessHandler;
    private final AuthenticationFailureHandler formAuthenticationFailureHandler;
    private final FilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(formAuthenticationProvider());
        auth.authenticationProvider(ajaxAuthenticationProvider());
    }

    @Bean
    public AuthenticationProvider formAuthenticationProvider() {
        return new FormAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Bean
    public AuthenticationProvider ajaxAuthenticationProvider() {
        return new AjaxAuthenticationProvider(userDetailsService, passwordEncoder());
    }

    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
//                .antMatchers("/mypage").hasRole("USER")
//                .antMatchers("/messages").hasRole("MANAGER")
//                .antMatchers("/config").hasRole("ADMIN")
//                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()

                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login_proc")
                .authenticationDetailsSource(formWebAuthenticationDetailsSource)
                .successHandler(formAuthenticationSuccessHandler)
                .failureHandler(formAuthenticationFailureHandler)
                .permitAll()

                .and()
                .exceptionHandling()
//                .authenticationEntryPoint(new AjaxLoginAuthenticationEntryPoint())
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                .accessDeniedPage("/denied")
                .accessDeniedHandler(accessDeniedHandler())

                .and()
                .addFilterAt(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class)
        ;

        http.csrf().disable();

        customConfigurer(http);
    }

    private void customConfigurer(HttpSecurity http) throws Exception {
        http
                .apply(new AjaxLoginConfigurer<>())
                .successHandlerAjax(ajaxAuthenticationSuccessHandler)
                .failureHandlerAjax(ajaxAuthenticationFailureHandler)
                .loginProcessingUrl("/api/login")
                .setAuthenticationManager(authenticationManagerBean());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public AccessDeniedHandler accessDeniedHandler() {
        FormAccessDeniedHandler commonAccessDeniedHandler = new FormAccessDeniedHandler();
        commonAccessDeniedHandler.setErrorPage("/denied");
        return commonAccessDeniedHandler;
    }

    @Bean
    public PermitAllFilter customFilterSecurityInterceptor() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter();
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
        permitAllFilter.setAccessDecisionManager(affirmativeBased());
        permitAllFilter.setAuthenticationManager(authenticationManagerBean());
        return permitAllFilter;
    }

    private AccessDecisionManager affirmativeBased() {
        return new AffirmativeBased(getAccessDecisionVoters());
    }

    private List<AccessDecisionVoter<?>> getAccessDecisionVoters() {
        return List.of(new RoleVoter());
    }
}
