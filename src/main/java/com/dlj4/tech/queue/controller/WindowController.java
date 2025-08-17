package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/window")
public class    WindowController {

    @Autowired
    WindowService windowService;
    @GetMapping("/list")
    public ResponseEntity<List<WindowResponse>> getWindowList() {

    return new ResponseEntity<List<WindowResponse>>(windowService.getWindowsList(), HttpStatus.OK);
}
    @PostMapping("/create")
    public ResponseEntity<WindowResponse> createWindow(@Valid @RequestBody WindowRequest windowRequest)
    {
        WindowResponse windowResponse= windowService.createWindow(windowRequest);

        return  new ResponseEntity<WindowResponse>(windowResponse,HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")

    public ResponseEntity<WindowResponse>  updateWindow(@PathVariable("id") Long id, @Valid @RequestBody WindowRequest windowRequest)
    {
        WindowResponse windowResponse=   windowService.updateWindow(id,windowRequest);

        return  new ResponseEntity<WindowResponse>(windowResponse,HttpStatus.CREATED);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteWindow(@PathVariable("id") Long id ){
        windowService.deleteWindow(id);

    }


}
