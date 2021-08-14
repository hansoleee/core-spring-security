package com.hansoleee.corespringsecurity.repository;

import com.hansoleee.corespringsecurity.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Account, Long> {
}
