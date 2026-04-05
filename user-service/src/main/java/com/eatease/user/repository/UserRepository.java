package com.eatease.user.repository;

import com.eatease.common.constants.Role;
import com.eatease.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRolesContaining(Role role);

    List<User> findByApprovedFalse();
}
