package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.dto.WindowRoleDTO;
import com.dlj4.tech.queue.entity.WindowRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WindowRoleRepository extends JpaRepository<WindowRole,Long> {



}
