package com.dlj4.tech.queue.repository;


import com.dlj4.tech.queue.entity.ConfigScreen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigScreen ,Long> {

}
