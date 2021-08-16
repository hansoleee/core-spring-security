package com.hansoleee.corespringsecurity.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AccountDto {

    private String id;
    private String username;
    private String email;
    private int age;
    private String password;
    private List<String> roles;

    @Builder
    public AccountDto(String id, String username, String email, int age, String password, List<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.age = age;
        this.password = password;
        this.roles = roles;
    }
}


