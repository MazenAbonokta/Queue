package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.constants.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    @Query("SELECT COALESCE(MAX(o.currentNumber), 0) FROM Order o WHERE o.service.id = :serviceId")
    Long findMaxCurrentNumberByServiceId(@Param("serviceId") Long serviceId);
     Order findFirstByOrderStatusAndServiceId( OrderStatus orderStatus,Long Service_Id);

    @Query(value = "SELECT * FROM orders o WHERE o.service_id IN :serviceIds " +
            "AND o.user_id = :userId " +
            "AND o.call_date IS NOT NULL " +

            "AND o.current_number = (SELECT MAX(o2.current_number) FROM orders o2 WHERE o2.service_id = o.service_id AND o2.user_id = :userId)",
            nativeQuery = true)
    List<Order> findByServiceIsInAndUserId(@Param("serviceIds") Set<Long> serviceIds, @Param("userId") Long userId);



    Optional<Order> findTopByUserIdOrderByCallDateDesc(Long userId);
    @Query("SELECT COUNT(u) FROM Order u WHERE u.orderStatus=?1 and u.service.id=?2")
long countByOrderStatusAAndServiceId(OrderStatus orderStatus,Long Service_Id);


}
