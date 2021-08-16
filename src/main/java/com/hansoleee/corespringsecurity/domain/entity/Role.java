package com.hansoleee.corespringsecurity.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "ROLE")
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"accounts", "resourcesSet"})
@EqualsAndHashCode(of = "id")
public class Role implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "role_id")
    private Long id;

    @Column(name = "role_name")
    private String roleName;

    @Column(name = "role_desc")
    private String roleDesc;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roleSet")
    @OrderBy("orderNum desc")
    private Set<Resources> resourcesSet = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "userRoles")
    private Set<Account> accounts = new HashSet<>();

    @Builder
    public Role(String roleName, String roleDesc, Set<Resources> resourcesSet, Set<Account> accounts) {
        this.roleName = roleName;
        this.roleDesc = roleDesc;
        this.resourcesSet = resourcesSet;
        this.accounts = accounts;
    }
}
