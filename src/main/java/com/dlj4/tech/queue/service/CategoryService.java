package com.dlj4.tech.queue.service;



import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.entity.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    public CategoryResponse createCategory(String name);
    public Category getCategoryById(Long id);
    public List<CategoryResponse> getCategorylist();
    public void deleteCategory(Long id);
    public void updatedCategory(Long id,String name);
}
