package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.entity.OrderAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OrderActionsRepository extends JpaRepository<OrderAction,Long> {

    @Query("SELECT oa FROM OrderAction oa " +
            "WHERE oa.orderStatus IN :statuses " +
            "AND oa.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "AND oa.createdAt = (SELECT MAX(subOa.createdAt) FROM OrderAction subOa WHERE subOa.order.id = oa.order.id) " +
            "ORDER BY oa.createdAt DESC")
    List<OrderAction> findTop10DistinctByOrderAndStatusAndCreatedAtToday(
            @Param("statuses") List<String> statuses,
            @Param("startOfDay") ZonedDateTime startOfDay,
            @Param("endOfDay") ZonedDateTime endOfDay);
}
