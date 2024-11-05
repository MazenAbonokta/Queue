package com.dlj4.tech.queue.controller;


import com.dlj4.tech.queue.dao.request.CategoryRequest;
import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> AddCategory(@RequestBody CategoryRequest categoryRequest)
    {
        CategoryResponse category= categoryService.createCategory(categoryRequest.getName());
        return new ResponseEntity<CategoryResponse>(category,HttpStatus.CREATED);
    }
    @PutMapping("/update/{id}")
    public void updateCategory(@RequestBody CategoryRequest categoryRequest)
    {
        categoryService.updatedCategory(categoryRequest.getId(), categoryRequest.getName());

    }
    @GetMapping("/list")
    public ResponseEntity<List<CategoryResponse>> getCategories()
    {
List<CategoryResponse> categories=categoryService.getCategorylist();
        return new ResponseEntity<List<CategoryResponse>>(categories,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public void deleteCategory(@PathVariable Long id)
    {
        categoryService.deleteCategory(id);

    }
}
