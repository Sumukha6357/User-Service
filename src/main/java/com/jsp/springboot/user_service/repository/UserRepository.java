package com.jsp.springboot.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.jsp.springboot.user_service.entity.User;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUserIdAndDeletedFalse(Long userId);

    boolean existsByUserNameIgnoreCaseAndDeletedFalse(String userName);

    boolean existsByUserNameIgnoreCaseAndUserIdNotAndDeletedFalse(String userName, Long userId);
}
