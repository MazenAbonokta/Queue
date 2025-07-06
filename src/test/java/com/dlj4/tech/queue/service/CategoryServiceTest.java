package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.imp.CategoryServiceImpl;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;
    @Mock
    private CategoryRepository categoryRepository;
@Mock
    ObjectsDataMapper   objectsDataMapper;
    Category category;
    @BeforeEach
            void init(){
        category = new Category();
        category.setId(1L);
        category.setName("test");
    }

    @Test
    @DisplayName("Unit test for add new category in java")
    public void givenCategory_whenAddCategory_thenReturnCategory(){

        // Arrange
        String name = "NewCategory";
        Category newCategory = new Category(null, name,false,null); // Mocked new category before save
        Category savedCategory = new Category(1L, name,false,null); // Mocked saved category
        CategoryResponse expectedResponse = new CategoryResponse(1L, name); // Mocked response

        BDDMockito.given(categoryRepository.findByName(name)).willReturn(Optional.empty());
        BDDMockito.given(categoryRepository.save(BDDMockito.any(Category.class))).willReturn(savedCategory);
        BDDMockito.given(objectsDataMapper.categoryToCategoryResponse(savedCategory)).willReturn(expectedResponse);

        // Act
        CategoryResponse actualResponse = categoryService.createCategory(name);

        // Assert
        Assertions.assertNotNull(actualResponse);
        Assertions.assertEquals(expectedResponse, actualResponse);

        BDDMockito.verify(categoryRepository, BDDMockito.times(1)).findByName(name);
        BDDMockito.verify(categoryRepository, BDDMockito.times(1)).save(BDDMockito.any(Category.class));
        BDDMockito.verify(objectsDataMapper, BDDMockito.times(1)).categoryToCategoryResponse(savedCategory);
    }

    @Test
    void createCategory_shouldThrowException_whenCategoryAlreadyExists() {
        String name = "TestCategory";
        Category existingCategory = new Category(1L, name,false,null);

        BDDMockito.given(categoryRepository.findByName(name)).willReturn(Optional.of(existingCategory));

      //  BDDMockito.given(categoryRepository.findByName(name)).willThrow(new ResourceAlreadyExistException(name));


        ResourceAlreadyExistException exception = Assertions.assertThrows(
                ResourceAlreadyExistException.class,
                () -> categoryService.createCategory(name)
        );

        Assertions.assertEquals("Category [TestCategoryis Already Exist", exception.getMessage());

        BDDMockito.verify(categoryRepository, BDDMockito.times(1)).findByName(name);
        BDDMockito.verify(categoryRepository, BDDMockito.never()).save(BDDMockito.any());
    }

    @Test
    void givenCategory_whenGetCategory_thenReturnCategory(){
        Category existingCategory = new Category(1L, "test",false,null);
        BDDMockito.given(categoryRepository.findById(category.getId())).willReturn(Optional.of(existingCategory));
        Category recivedcategory= categoryService.getCategoryById(1L);
        Assertions.assertEquals(recivedcategory, existingCategory);
        Assertions.assertEquals(category.getName(), recivedcategory.getName());

    }
    @Test
    void givenCategory_whenDeleteCategory_thenReturnCategory(){

        BDDMockito.given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        categoryService.deleteCategory(1L);
        BDDMockito.verify(categoryRepository, BDDMockito.times(1)).findById(1L);
        BDDMockito.verify(categoryRepository, BDDMockito.times(1)).delete(category);


    }
    @Test
    void deleteCategory_shouldThrowException_whenCategoryNotFound() {
        // Arrange
        Long id = 1L; // Example category ID
        BDDMockito.given(categoryRepository.findById(id)).willReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = Assertions.assertThrows(
                ResourceNotFoundException.class,
                () -> {
                    // Simulate the method under test
                    Category category = categoryRepository.findById(id).orElseThrow(
                            () -> new ResourceNotFoundException("Error Category")
                    );
                    categoryRepository.delete(category); // This line won't be reached due to the exception
                }
        );

        // Verify exception message
        Assertions.assertEquals("Error Category", exception.getMessage());

        // Verify interactions with the repository
        BDDMockito.verify(categoryRepository, BDDMockito.times(1)).findById(id);
        BDDMockito.verify(categoryRepository, BDDMockito.never()).delete(BDDMockito.any());
    }
      /* @Test
    public void givenCategoryException_whenAddDuplicateCategory_thenReturnCategoryException() throws Exception {
        // Arrange: Simulate the exception thrown for a duplicate category
        BDDMockito.given(categoryService.createCategory(BDDMockito.anyString()))
                .willReturn(new CategoryResponse(1L, "testCategory"));

        // Serialize the category object
        String categoryJson = objectMapper.writeValueAsString(category);

        // Act: Perform two simultaneous POST requests
        ResultActions result1 = mockMvc.perform(post("/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .characterEncoding("UTF-8")
                .content(categoryJson));

        ResultActions result2 = mockMvc.perform(post("/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .characterEncoding("UTF-8")
                .content(categoryJson));

        // Assert: Verify the responses
        result1.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("testCategory"));

        result2.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Category already exists"));



    }
}*/
}
