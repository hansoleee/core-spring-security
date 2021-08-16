package com.hansoleee.corespringsecurity.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"userRoles"})
public class Account implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private int age;

    @Column
    private String password;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @JoinTable(name = "account_roles",
            joinColumns = {
                    @JoinColumn(name = "account_id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "role_id")})
    private Set<Role> userRoles = new HashSet<>();

    @Builder
    public Account(String username, String email, int age, String password, Set<Role> userRoles) {
        this.username = username;
        this.email = email;
        this.age = age;
        this.password = password;
        this.userRoles = userRoles;
    }
}
