package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dto.ServiceDTO;
import com.dlj4.tech.queue.dto.WindowRoleDTO;
import org.springframework.stereotype.Service;

@Service
public interface ServiceService {
    public void createService(ServiceDTO serviceDTO);

    public com.dlj4.tech.queue.entity.Service getServiceById(Long serviceID);


}
