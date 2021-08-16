package com.hansoleee.corespringsecurity.domain.dto;

import com.hansoleee.corespringsecurity.domain.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class ResourcesDto {

    private String id;
    private String resourceName;
    private String httpMethod;
    private int orderNum;
    private String resourceType;
    private String roleName;
    private Set<Role> roleSet;

    @Builder
    public ResourcesDto(String id, String resourceName, String httpMethod, int orderNum, String resourceType, String roleName, Set<Role> roleSet) {
        this.id = id;
        this.resourceName = resourceName;
        this.httpMethod = httpMethod;
        this.orderNum = orderNum;
        this.resourceType = resourceType;
        this.roleName = roleName;
        this.roleSet = roleSet;
    }
}
