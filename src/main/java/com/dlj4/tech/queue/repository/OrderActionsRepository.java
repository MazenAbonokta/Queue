package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.entity.OrderAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderActionsRepository extends JpaRepository<OrderAction,Long> {
}
