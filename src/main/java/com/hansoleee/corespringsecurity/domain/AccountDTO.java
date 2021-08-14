package com.hansoleee.corespringsecurity.domain;

import lombok.Data;

@Data
public class AccountDTO {

    private String username;
    private String password;
    private String email;
    private int age;
    private String role;
}
