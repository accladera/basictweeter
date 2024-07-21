package com.accladera.springsecurity.repository;

import com.accladera.springsecurity.entities.UserP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserP, UUID> {
    Optional<UserP> findByUsername(String username);
}
