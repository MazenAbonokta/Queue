package com.dlj4.tech.queue.repository;


import com.dlj4.tech.queue.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity,Long> {

    List<ServiceEntity> findAllByIdIn(List<Long> ids);
}
