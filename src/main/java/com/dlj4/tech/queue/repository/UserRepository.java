package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
    void  deleteAllById(Long id);

}
