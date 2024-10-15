package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/window")
public class WindowController {

    @Autowired
    WindowService windowService;
    @GetMapping("/list")
    public ResponseEntity<List<WindowResponse>> getWindowList() {

    return new ResponseEntity<List<WindowResponse>>(windowService.getWindowsList(), HttpStatus.OK);
}
    @PostMapping("/create")
    public ResponseEntity<String> createWindow(@RequestBody WindowRequest windowRequest)
    {
        windowService.createWindow(windowRequest);

        return  new ResponseEntity<String>("the window has been created",HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")

    public ResponseEntity<String> updateWindow(@PathVariable("id") Long id, @RequestBody  WindowRequest windowRequest)
    {
        windowService.updateWindow(id,windowRequest);

        return  new ResponseEntity<String>("the window has been update",HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteWindow(@PathVariable("id") Long id ){
        windowService.deleteWindow(id);
        return new ResponseEntity<String>("The Window has been deleted" , HttpStatus.OK);
    }


}
