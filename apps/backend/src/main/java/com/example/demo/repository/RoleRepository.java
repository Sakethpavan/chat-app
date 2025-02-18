package com.example.demo.repository;

import com.example.demo.model.AppRole;
import com.example.demo.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, Integer> {
    Optional<Role> findByRoleName(AppRole role);
}
