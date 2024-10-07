package com.dlj4.tech.queue.service;


import com.dlj4.tech.queue.entity.Category;

import java.util.Optional;

public interface CategoryService {
    public void createCategory(String name);
    public Category getCategoryById(Long id);
}
