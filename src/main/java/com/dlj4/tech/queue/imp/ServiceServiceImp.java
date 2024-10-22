package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.entity.Category;

import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.ServiceRepository;
import com.dlj4.tech.queue.service.CategoryService;
import com.dlj4.tech.queue.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServiceServiceImp implements ServiceService {
    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    CategoryService categoryService;

    @Override
    public ServiceResponse createService(ServiceRequest serviceRequest) {

       ServiceEntity service= serviceRepository.save(getServiceEntity(serviceRequest));
        return  objectsDataMapper.ServiceToServiceResponse(service);
    }

    @Override
    public ServiceEntity getServiceById(Long serviceID) {
        Optional<ServiceEntity> service = serviceRepository.findById(serviceID);
        if (service.isEmpty())
        {
            throw  new ResourceNotFoundException("service ["+serviceID +"is not Exist");
        }
        return service.get();
    }

    @Override
    public void updateService(Long id, ServiceRequest serviceRequest) {
        ServiceEntity service = serviceRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("The Service is not found")

        );
        Category category= categoryService.getCategoryById(serviceRequest.getCategoryId());
        service= objectsDataMapper.copyServiceRequestToServiceEntity(serviceRequest,service,category);
        serviceRepository.save(service);

    }

    @Override
    public void deleteService(Long id) {
        ServiceEntity serviceEntity= getServiceById(id);

        serviceRepository.delete(serviceEntity);
    }

    @Override
    public List<ServiceResponse> getServices() {

        List<ServiceEntity> serviceEntities= serviceRepository.findAll();
        List<ServiceResponse> responses = serviceEntities.stream().map(
                objectsDataMapper::ServiceToServiceResponse
        ).collect(Collectors.toList());
        return  responses;
    }

    @Override
    public List<ServiceEntity> getServicesByIds(List<Long> Ids) {
        return serviceRepository.findAllByIdIn(Ids);
    }


    ServiceEntity getServiceEntity(ServiceRequest serviceRequest){
        Category category=categoryService.getCategoryById(serviceRequest.getCategoryId());

        ServiceEntity service = objectsDataMapper.serviceDTOToServiceEntity(serviceRequest,category);
        return service;
    }
}
