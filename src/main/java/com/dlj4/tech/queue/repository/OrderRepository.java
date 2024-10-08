package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("SELECT COALESCE(MAX(o.currentNumber), 0) FROM Order o WHERE o.service.id = :serviceId")
    Long findMaxCurrentNumberByServiceId(@Param("ServiceId") Long ServiceId);
}
