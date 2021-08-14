package com.hansoleee.corespringsecurity.password;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

public class PasswordEncoderTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() throws Exception {
        passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Test
    void encode() throws Exception {
        String rawPassword = "password";

        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("encodedPassword = " + encodedPassword);

        assertThat(passwordEncoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(encodedPassword).contains("{bcrypt}");
    }

    @Test
    void match() throws Exception {
        String rawPassword = "password";

        String bcryptEncodedPassword = "{bcrypt}$2a$10$.66QnNuK52on7s8gaREmYeN5ICm0pZ36b5PBz/rIlqNVv1xa2BIui";
        String pbkdf2EncodedPassword = "{pbkdf2}7a07c208fc2a407fb89cc3b6effb1b759da575a85f65dda9cd426f1ad14b56e6afaeeea6f9269569";
        String sha256EncodedPassword = "{sha256}fcef9e3f82af42d9059e74a95c633fe99b7aba1c4bfb9ac1cae31dd1b67060da933776fee8baec8f";

        assertThat(passwordEncoder.matches(rawPassword, bcryptEncodedPassword)).isTrue();
        assertThat(passwordEncoder.matches(rawPassword, pbkdf2EncodedPassword)).isTrue();
        assertThat(passwordEncoder.matches(rawPassword, sha256EncodedPassword)).isTrue();
    }

    @Test
    void Default_PasswordEncoder을_사용한_경우는_passwordEncode_ID가_없다() throws Exception {
        String rawPassword = "a";
        String encodedPassword = "2291c54a5d7d698796cf04ecbd51f402a904dcf906ae071f05f5a01a289f8cef0acb93ef4e805d41";

        DelegatingPasswordEncoder delegatingPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        delegatingPasswordEncoder.setDefaultPasswordEncoderForMatches(new Pbkdf2PasswordEncoder());

        assertThat(delegatingPasswordEncoder.matches(rawPassword, encodedPassword)).isTrue();
    }
}
