package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.entity.ServiceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceService {
    public ServiceResponse createService(ServiceRequest serviceRequest);

    public ServiceEntity getServiceById(Long serviceID);
    public void updateService(Long id,ServiceRequest serviceRequest);
    public void deleteService(Long id );
    public List<ServiceResponse> getServices( );
    public List<ServiceEntity> getServicesByIds(List<Long> Ids);

}
