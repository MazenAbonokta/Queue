package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.CategoryRepository;
import com.dlj4.tech.queue.repository.ServiceRepository;
import com.dlj4.tech.queue.service.CategoryService;
import com.dlj4.tech.queue.service.ServiceService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ServiceServiceImpTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ObjectsDataMapper objectsDataMapper;

    @InjectMocks
    private ServiceServiceImp serviceService;
    Category category;
    ServiceEntity serviceEntity;
    ServiceResponse serviceResponse;
    ServiceRequest serviceRequest;
    @BeforeEach
     void setUp() {
         category = new Category();
        category.setId(1L);
        category.setName("test");
        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setId(1L);
        serviceEntity.setName("test");
        serviceEntity.setCategory(category);
        serviceEntity.setCode("A");
        serviceEntity.setStart(1);
        serviceEntity.setEndTime(LocalTime.now());
        serviceResponse  = new ServiceResponse(1L, "A", 1, 50, 1L, "Set", "test", "10:52", 1L);

         serviceRequest = new ServiceRequest();
        serviceRequest.setName("test");
        serviceRequest.setId(1L);
        serviceRequest.setEnd(50);
        serviceRequest.setCategoryId(1L);
        serviceRequest.setCode("A");
        serviceRequest.setStart(1);
        serviceRequest.setEndTime("10:52");

    }
    @Test
    void createService() {
        // Arrange






        // Mocking behavior
      //  BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        BDDMockito.given(categoryService.getCategoryById(1L)).willReturn(category);
        BDDMockito.given(objectsDataMapper.serviceDTOToServiceEntity(serviceRequest,category))
                .willReturn(serviceEntity);
        BDDMockito.given(serviceRepository.save(BDDMockito.any(ServiceEntity.class))).willReturn(serviceEntity); // Ensure it accepts any ServiceEntity

        BDDMockito.given(objectsDataMapper.ServiceToServiceResponse(serviceEntity))
                .willReturn(serviceResponse);

        // Act
        ServiceResponse serviceResponse1 = serviceService.createService(serviceRequest);

        // Assert
        Assertions.assertNotNull(serviceResponse1);
        Assertions.assertEquals(serviceResponse, serviceResponse1);
        Assertions.assertEquals(1L, serviceResponse1.getId());
    }
    @Test
    void updateService() {

        BDDMockito.given(serviceRepository.findById(1L)).willReturn(Optional.of(serviceEntity));
BDDMockito.given(categoryService.getCategoryById(1L)).willReturn(category);
BDDMockito.given(objectsDataMapper.copyServiceRequestToServiceEntity(serviceRequest, serviceEntity,category));
BDDMockito.doNothing().when(serviceRepository.save(BDDMockito.any(ServiceEntity.class)));
        ServiceResponse response = serviceService.updateService(1L, serviceRequest);

        // Verify the results
        assertNotNull(response);
        assertEquals(serviceResponse.getId(), response.getId());


        // Verify method interactions
        verify(serviceRepository, times(1)).findById(1L);
        verify(categoryService, times(1)).getCategoryById(serviceRequest.getCategoryId());
        verify(objectsDataMapper, times(1)).copyServiceRequestToServiceEntity(serviceRequest, serviceEntity, category);
        verify(serviceRepository, times(1)).save(serviceEntity);
        verify(objectsDataMapper, times(1)).ServiceToServiceResponse(serviceEntity);

    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    @EnabledIfEnvironmentVariable(named = "USER",matches = "test")
    void dependencyTest() {

        assertAll("Test all",
                ()->assertEquals(1L,serviceRequest.getId(),"done")
                );
    }
}