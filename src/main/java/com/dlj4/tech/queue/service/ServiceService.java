package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.ServiceDAO;
import org.springframework.stereotype.Service;

@Service
public interface ServiceService {
    public void createService(ServiceDAO serviceDAO);

    public com.dlj4.tech.queue.entity.Service getServiceById(Long serviceID);


}
