package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.ServiceRequest;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
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
            ServiceResponse serviceResponse=serviceService.createService(request);
        return new ResponseEntity<ServiceResponse>(serviceResponse , HttpStatus.CREATED);
    }
    @PutMapping("/edit/")
    public ResponseEntity<String> updateService( @RequestBody ServiceRequest request){
        serviceService.updateService(request.getId(),request);
        return new ResponseEntity<String>("The Service has been Updated" , HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteService(@PathVariable("id") Long id ){
        serviceService.deleteService(id);
        return new ResponseEntity<String>("The Service has been deleted" , HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<ServiceResponse>> getServices(){
        return new ResponseEntity<List<ServiceResponse> >(serviceService.getServices(),HttpStatus.OK);
    }
}
