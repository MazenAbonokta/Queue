package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @PostMapping("/AddCategory")
    public ResponseEntity<String> AddCategory(@RequestBody String name)
    {
        categoryService.createCategory(name);
        return new ResponseEntity<String>("The Category has been created",HttpStatus.CREATED);
    }

}
