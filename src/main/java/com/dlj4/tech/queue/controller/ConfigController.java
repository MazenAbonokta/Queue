package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.ConfigRequest;
import com.dlj4.tech.queue.dao.response.ConfigResponse;
import com.dlj4.tech.queue.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("config")
public class ConfigController {
    @Autowired
    ConfigService configService;
    @PostMapping("/upload-screen-config")
    public ResponseEntity<ConfigResponse> UploadScreenConfig(@RequestBody ConfigRequest configRequest) {

        if(configRequest.getId().toString()=="")
        {
         return  ResponseEntity.ok(configService.createConfig(configRequest));
        }
        else{
            return  ResponseEntity.ok(configService.updateConfig(configRequest));
        }

    }
    @PutMapping("/update-screen-config")
    public ResponseEntity<ConfigResponse> UpdateScreenConfig(@RequestBody ConfigRequest configRequest) {

        if(configRequest.getId().toString()=="")
        {
            return  ResponseEntity.ok(configService.createConfig(configRequest));
        }
        else{
            return  ResponseEntity.ok(configService.updateConfig(configRequest));
        }

    }
    @GetMapping("/get-config")
    public ResponseEntity<ConfigResponse> GetConfigBy() {
       return ResponseEntity.ok(configService.getConfig());
    }


}
