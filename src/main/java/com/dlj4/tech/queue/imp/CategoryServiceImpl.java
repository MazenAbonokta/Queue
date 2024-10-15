package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.repository.CategoryRepository;
import com.dlj4.tech.queue.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Override
    public Category createCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isPresent())
        {
            throw  new ResourceAlreadyExistException("Category ["+name +"is Already Exist");
        }
      Category createdCategory =  categoryRepository.save(createNewCategory(name));
        return createdCategory;
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isPresent())
        {
            throw  new ResourceNotFoundException("Category ["+id +"is not Already Exist");
        }
        return category.get();
    }

    @Override
    public List<Category> getCategorylist() {
        return categoryRepository.findAll();
    }

    @Override
    public void deleteCategory(Long id) {
        Category category =categoryRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Error Category")
        );
        categoryRepository.delete(category);
    }

    private Category createNewCategory(String name) {
        Category newCategory = new Category();

        newCategory.setName(name);
        return newCategory;
    }
}
