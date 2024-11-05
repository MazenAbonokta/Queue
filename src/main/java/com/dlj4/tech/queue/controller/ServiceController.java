package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("service")
public class ServiceController {

    @Autowired
    ServiceService serviceService;
    @PostMapping("/create")
    public ResponseEntity<ServiceResponse> createService(@RequestBody ServiceRequest request){
        //throw new ResourceNotFoundException("No");
         ServiceResponse serviceResponse=serviceService.createService(request);
        return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ServiceResponse>  updateService( @RequestBody ServiceRequest request){
        ServiceResponse serviceResponse =  serviceService.updateService(request.getId(),request);
        return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public  void deleteService(@PathVariable("id") Long id ){
        serviceService.deleteService(id);

    }

    @GetMapping("/list")
    public ResponseEntity<List<ServiceResponse>> getServices(){
        return new ResponseEntity<List<ServiceResponse> >(serviceService.getServices(),HttpStatus.OK);
    }
}
