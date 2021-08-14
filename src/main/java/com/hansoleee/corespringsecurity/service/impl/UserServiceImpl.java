package com.hansoleee.corespringsecurity.service.impl;

import com.hansoleee.corespringsecurity.domain.Account;
import com.hansoleee.corespringsecurity.repository.UserRepository;
import com.hansoleee.corespringsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void createUser(Account account) {
        userRepository.save(account);
    }
}
