package com.dlj4.tech.queue.controller;


import com.dlj4.tech.queue.dao.request.CategoryRequest;
import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

/**
 * Category Management Controller
 * 
 * Manages service categories for organizing services into logical groups.
 * Categories help organize services like Government, Banking, Healthcare, etc.
 * 
 * Base URL: /category
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Category Management", description = "CRUD operations for service categories with validation")
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Operation(
            summary = "Create New Category",
            description = "Create a new service category with name validation for organizing services",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Category created successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "name": "Government Services"
                                    }"""))),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "details": {
                                        "name": "Category name is required and cannot be empty"
                                      }
                                    }"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/add")
    public ResponseEntity<CategoryResponse> AddCategory(
            @Parameter(description = "Category details with name validation", required = true)
            @Valid @RequestBody CategoryRequest categoryRequest)
    {
        CategoryResponse category= categoryService.createCategory(categoryRequest.getName());
        return new ResponseEntity<CategoryResponse>(category,HttpStatus.CREATED);
    }
    
    /**
     * Update Existing Category
     * 
     * Updates category information. Category ID is required in request body.
     * 
     * @param categoryRequest CategoryRequest with updated category details (must include ID)
     * 
     * @apiNote PUT /category/update
     * @apiBody {
     *   "id": "number (positive, required)",
     *   "name": "string (2-50 chars, letters/numbers/spaces/basic punctuation)"
     * }
     * @apiSuccess 200 Category successfully updated
     * @apiError 400 Validation errors with detailed field messages
     * @apiError 404 "Category not found"
     * @apiError 500 "Failed to update category"
     */
    @Operation(
            summary = "Update Category",
            description = "Update existing category information with name validation",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "400", description = "Validation errors",
                    content = @Content(schema = @Schema(implementation = com.dlj4.tech.queue.dao.response.ErrorResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "status": 400,
                                      "error": "Validation Failed",
                                      "details": {
                                        "name": "Category name must be between 2 and 50 characters"
                                      }
                                    }"""))),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update")
    public void updateCategory(
            @Parameter(description = "Category details with ID and updated name", required = true)
            @Valid @RequestBody CategoryRequest categoryRequest)
    {
        categoryService.updatedCategory(categoryRequest.getId(), categoryRequest.getName());
    }
    
    /**
     * Get All Categories
     * 
     * Retrieves list of all active categories.
     * 
     * @return List of CategoryResponse objects
     * 
     * @apiNote GET /category/list
     * @apiSuccess 200 Array of CategoryResponse objects
     * @apiError 500 "Failed to retrieve categories"
     */
    @Operation(
            summary = "Get All Categories",
            description = "Retrieve list of all active categories for service organization"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class),
                            examples = @ExampleObject(value = """
                                    [{
                                      "id": 1,
                                      "name": "Government Services"
                                    },
                                    {
                                      "id": 2,
                                      "name": "Banking Services"
                                    }]"""))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/list")
    public ResponseEntity<List<CategoryResponse>> getCategories()
    {
        List<CategoryResponse> categories=categoryService.getCategorylist();
        return new ResponseEntity<List<CategoryResponse>>(categories,HttpStatus.OK);
    }
    
    /**
     * Delete Category
     * 
     * Soft deletes a category by ID. Category will be marked as deleted but not removed from database.
     * 
     * @param id Category ID to delete (must be positive number)
     * 
     * @apiNote DELETE /category/delete/{id}
     * @apiSuccess 200 Category successfully deleted
     * @apiError 400 "Invalid category ID"
     * @apiError 404 "Category not found"
     * @apiError 500 "Failed to delete category"
     */
    @Operation(
            summary = "Delete Category",
            description = "Soft delete a category by ID - category will be marked as deleted",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid category ID"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Category cannot be deleted - has associated services"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/delete/{id}")
    public void deleteCategory(
            @Parameter(description = "Category ID to delete", required = true, example = "1")
            @PathVariable Long id)
    {
        categoryService.deleteCategory(id);
    }
}
