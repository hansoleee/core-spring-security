package com.hansoleee.corespringsecurity.service;

import com.hansoleee.corespringsecurity.domain.entity.Resources;
import com.hansoleee.corespringsecurity.domain.entity.Role;
import com.hansoleee.corespringsecurity.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SecurityResourceService {

    private final ResourcesRepository resourcesRepository;

    @Transactional(readOnly = true)
    public Map<RequestMatcher, List<ConfigAttribute>> getResourceList() {
        Map<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> allResources = resourcesRepository.findAllResources();
        allResources.forEach(resources -> result.put(new AntPathRequestMatcher(resources.getResourceName()),
                resources.getRoleSet().stream().map(Role::getRoleName).map(SecurityConfig::new).collect(Collectors.toList())));

        return result;
    }
}
