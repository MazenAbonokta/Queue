package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.ServiceDAO;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.ServiceRepository;
import com.dlj4.tech.queue.service.CategoryService;
import com.dlj4.tech.queue.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceServiceImp implements ServiceService {
    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    CategoryService categoryService;

    @Override
    public void createService(ServiceDAO serviceDAO) {

        serviceRepository.save(getServiceEntity(serviceDAO));

    }

    @Override
    public com.dlj4.tech.queue.entity.Service getServiceById(Long serviceID) {
        Optional<com.dlj4.tech.queue.entity.Service> service = serviceRepository.findById(serviceID);
        if (service.isPresent())
        {
            throw  new ResourceNotFoundException("service ["+serviceID +"is not Exist");
        }
        return service.get();
    }


    com.dlj4.tech.queue.entity.Service getServiceEntity(ServiceDAO serviceDAO){
        Category category=categoryService.getCategoryById(serviceDAO.getCategoryId());

        com.dlj4.tech.queue.entity.Service service = objectsDataMapper.serviceDTOToServiceEntity(serviceDAO,category);
        return service;
    }
}
