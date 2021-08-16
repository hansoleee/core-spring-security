package com.hansoleee.corespringsecurity.repository;

import com.hansoleee.corespringsecurity.domain.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Account, Long> {

    @EntityGraph(attributePaths = {"userRoles"})
    Optional<Account> findByUsername(String username);

    int countByUsername(String username);
}