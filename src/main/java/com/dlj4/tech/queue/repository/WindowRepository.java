package com.dlj4.tech.queue.repository;

import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.entity.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WindowRepository extends JpaRepository<Window,Long> {
    Optional<Window> findByIpAddress(String ipAddress);

}
