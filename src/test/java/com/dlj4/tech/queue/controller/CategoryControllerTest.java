package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.config.QueueConfigurationTest;
import com.dlj4.tech.queue.config.TestSecurityConfig;
import com.dlj4.tech.queue.constants.Role;
import com.dlj4.tech.queue.dao.request.CategoryRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.response.CategoryResponse;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;


import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest

@Import({QueueConfigurationTest.class, TestSecurityConfig.class})
public class CategoryControllerTest {
   /* @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean
    private AuthenticationManager authenticationManager;*/
    @Autowired
    MockMvc mockMvc;
    @MockBean
    CategoryService categoryService;
    @MockBean
    AuthenticationService authenticationService;

    @Autowired
    ObjectMapper objectMapper;

    CategoryRequest category = new CategoryRequest();
    String token="";
    @BeforeEach
    void setUp() throws Exception {

        category.setId(1L);
        category.setName("testCategory");
        // Prepare mock user data
        UserRequest userRequest = new UserRequest(
                "admin", "admin", "admin@gmail.com", "00000", "admin", "Active", "", "", Role.ADMIN.toString()
        );

        // Mock the sign-up process (no real persistence required in this test)
        BDDMockito.given(authenticationService.signUp(userRequest)).willReturn(null);

        // Prepare mock JwtAuthenticationResponse
        JwtAuthenticationResponse mockJwtResponse = new JwtAuthenticationResponse();
        mockJwtResponse.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        mockJwtResponse.setRefreshToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c");
        mockJwtResponse.setExpiresAt(new Date(System.currentTimeMillis() + 3600 * 1000)); // 1 hour expiry
        mockJwtResponse.setStatus("Success");
        mockJwtResponse.setWindowId(1L);
        mockJwtResponse.setWindowNumber("A1");
        mockJwtResponse.setRole(Role.ADMIN.toString());

        // Mock the sign-in process
        BDDMockito.given(authenticationService.signIn(BDDMockito.any(SigningRequest.class)))
                .willReturn(mockJwtResponse);

        // Authenticate and get token
        token = authenticateAndGetToken();

    }
    @Test
    public void  givenCategory_whenAddCategory_thenReturnCategory() throws Exception {
     /*   BDDMockito.given(categoryService.createCategory(BDDMockito.anyString()))
                .willReturn(new CategoryResponse(1L, "testCategory"));
        ResultActions resultActions=mockMvc.perform(post("/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .characterEncoding("UTF-8")
                .contentType(objectMapper.writeValueAsString(category))
        );

        resultActions.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("testCategory"));*/


       BDDMockito.given(categoryService.createCategory(BDDMockito.anyString()))
                .willReturn(new CategoryResponse(1L, "testCategory"));

        // Perform the POST request
        mockMvc.perform(post("/category/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .characterEncoding("UTF-8")// Correct Content-Type
                        .content(objectMapper.writeValueAsString(category)))
                .andDo(print())// Serialize category to JSON
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("testCategory"));
    }

    @Test
    public void givenCategoryException_whenAddDuplicateCategory_thenReturnCategoryException() throws Exception {
        BDDMockito.given(categoryService.createCategory(BDDMockito.anyString()))
                .willThrow(new ResourceAlreadyExistException("Category already exists"));

        mockMvc.perform(post("/category/add")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .characterEncoding("UTF-8")// Correct Content-Type
                .content(objectMapper.writeValueAsString(category)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("Category already exists"));


    }
    @Test
    public void givenCategory_whenUpdateCategory_thenReturnCategory() throws Exception {
        BDDMockito.willDoNothing().given(categoryService).updatedCategory(BDDMockito.anyLong(), BDDMockito.anyString());

        mockMvc.perform(put("/category/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .characterEncoding("UTF-8")// Correct Content-Type
                        .content(objectMapper.writeValueAsString(category)))
                .andDo(print());

    }

    @Test
    public void givenCategory_whenGetCategories_thenReturnCategory() throws Exception {

        List<CategoryResponse> mockedCategories = List.of(
                new CategoryResponse(1L, "Category 1"),
                new CategoryResponse(2L, "Category 2")
        );
        BDDMockito.given(categoryService.getCategorylist())
                .willReturn(mockedCategories);

        mockMvc.perform(get("/category/list")
                .header("Authorization", "Bearer " + token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()",
                        CoreMatchers.is(mockedCategories.size())));

    }

    private String authenticateAndGetToken() throws Exception {
        String signInRequestJson = """
            {
                "username": "admin",
                "password": "admin"
            }
        """;

        MvcResult result = mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signInRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        return JsonPath.read(responseBody, "$.token"); // Extract the token field from the response
    }
}
