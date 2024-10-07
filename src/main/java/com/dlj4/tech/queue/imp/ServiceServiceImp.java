package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dto.ServiceDTO;
import com.dlj4.tech.queue.dto.WindowRoleDTO;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.entity.WindowRole;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.ServiceRepository;
import com.dlj4.tech.queue.service.CategoryService;
import com.dlj4.tech.queue.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public void createService(ServiceDTO serviceDTO) {

        serviceRepository.save(getServiceEntity(serviceDTO));

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


    com.dlj4.tech.queue.entity.Service getServiceEntity(ServiceDTO serviceDTO){
        Category category=categoryService.getCategoryById(serviceDTO.getCategoryId());

        com.dlj4.tech.queue.entity.Service service = objectsDataMapper.serviceDTOToServiceEntity(serviceDTO,category);
        return service;
    }
}
