package com.dlj4.tech.queue.imp;


import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.CategoryRepository;
import com.dlj4.tech.queue.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Override
    public CategoryResponse createCategory(String name) {
        Optional<Category> category = categoryRepository.findByName(name);
        if (category.isPresent())
        {
            throw  new ResourceAlreadyExistException("Category ["+name +"is Already Exist");
        }
      Category createdCategory =  categoryRepository.save(createNewCategory(name));
        return objectsDataMapper.categoryToCategoryResponse(createdCategory);
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty())
        {
            throw  new ResourceNotFoundException("Category ["+id +"]is not  Exist");
        }
        return category.get();
    }

    @Override
    public List<CategoryResponse> getCategorylist() {

        List<CategoryResponse> responses= categoryRepository.findAll().stream().map(
                category -> objectsDataMapper.categoryToCategoryResponse(category)
        ).collect(Collectors.toList());
        return responses;
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
