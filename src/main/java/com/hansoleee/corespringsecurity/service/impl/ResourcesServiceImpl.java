package com.hansoleee.corespringsecurity.service.impl;

import com.hansoleee.corespringsecurity.domain.entity.Resources;
import com.hansoleee.corespringsecurity.repository.ResourcesRepository;
import com.hansoleee.corespringsecurity.service.ResourcesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ResourcesServiceImpl implements ResourcesService {

    private final ResourcesRepository resourcesRepository;

    @Transactional(readOnly = true)
    public Resources getResources(long id) {
        return resourcesRepository.findById(id).orElse(new Resources());
    }

    @Transactional(readOnly = true)
    public List<Resources> getResources() {
        return resourcesRepository.findAll(Sort.by(Sort.Order.asc("orderNum")));
    }

    public void createResources(Resources resources){
        resourcesRepository.save(resources);
    }

    public void deleteResources(long id) {
        resourcesRepository.deleteById(id);
    }
}
