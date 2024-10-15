package com.dlj4.tech.queue.controller;


import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @PostMapping("/addCategory")
    public ResponseEntity<Category> AddCategory(@RequestBody String name)
    {
        Category category= categoryService.createCategory(name);
        return new ResponseEntity<Category>(category,HttpStatus.CREATED);
    }
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories()
    {

        return new ResponseEntity<List<Category>>(categoryService.getCategorylist(),HttpStatus.OK);
    }
    @DeleteMapping("/deleteCategory/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id)
    {
        categoryService.deleteCategory(id);
        return new ResponseEntity<String>("Category has been deleted",HttpStatus.OK);
    }
}
