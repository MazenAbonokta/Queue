package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.response.*;
import com.dlj4.tech.queue.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Dashboard Analytics Controller
 * 
 * Provides comprehensive dashboard endpoints for real-time analytics, statistics,
 * and queue management data. Supports Angular frontend with CORS configuration.
 * 
 * Features:
 * - Real-time queue statistics and summaries
 * - Entity management (Categories, Services, Windows, Users, Orders)
 * - Recent activity tracking and analytics
 * - Transfer request management
 * - Live queue monitoring
 * 
 * Base URL: /dashboard
 * 
 * @author Queue Management System
 * @version 1.0
 */
@Tag(name = "Dashboard Analytics", description = "Real-time analytics, statistics, and queue management data for dashboard interfaces")
@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = {"http://localhost:4200", "http://127.0.0.1:4200"}, 
             allowedHeaders = {"Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With", "x-access-token"},
             allowCredentials = "true")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    // ==================== SUMMARY STATISTICS ====================
    
    @Operation(
            summary = "Get Dashboard Summary Per Day",
            description = "Retrieve dashboard statistics broken down by day for the last 30 days (one month)",
            security = {@SecurityRequirement(name = "JWT Authentication"), @SecurityRequirement(name = "Custom Token Authentication")}
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Daily dashboard summaries retrieved successfully",
                    content = @Content(schema = @Schema(implementation = DashboardSummaryResponse.class))),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/summary")
    public ResponseEntity<List<DashboardSummaryResponse>> getDashboardSummary() {
        List<DashboardSummaryResponse> dailySummaries = dashboardService.getDashboardSummary();
        return new ResponseEntity<>(dailySummaries, HttpStatus.OK);
    }

    // ==================== ALL ENTITIES LISTS ====================
    
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> categories = dashboardService.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> getAllServices() {
        List<ServiceResponse> services = dashboardService.getAllServices();
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/windows")
    public ResponseEntity<List<WindowResponse>> getAllWindows() {
        List<WindowResponse> windows = dashboardService.getAllWindows();
        return new ResponseEntity<>(windows, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = dashboardService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> orders = dashboardService.getAllOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/transfer-requests")
    public ResponseEntity<List<TransferResponse>> getAllTransferRequests() {
        List<TransferResponse> transfers = dashboardService.getAllTransferRequests();
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    @GetMapping("/window-roles")
    public ResponseEntity<List<WindowRoleResponse>> getAllWindowRoles() {
        List<WindowRoleResponse> windowRoles = dashboardService.getAllWindowRoles();
        return new ResponseEntity<>(windowRoles, HttpStatus.OK);
    }

    @GetMapping("/order-actions")
    public ResponseEntity<List<OrderActionResponse>> getAllOrderActions() {
        List<OrderActionResponse> orderActions = dashboardService.getAllOrderActions();
        return new ResponseEntity<>(orderActions, HttpStatus.OK);
    }

    @GetMapping("/order-archives")
    public ResponseEntity<List<OrderArchiveResponse>> getAllOrderArchives() {
        List<OrderArchiveResponse> orderArchives = dashboardService.getAllOrderArchives();
        return new ResponseEntity<>(orderArchives, HttpStatus.OK);
    }

    @GetMapping("/user-actions")
    public ResponseEntity<List<UserActionsResponse>> getAllUserActions() {
        List<UserActionsResponse> userActions = dashboardService.getAllUserActions();
        return new ResponseEntity<>(userActions, HttpStatus.OK);
    }

    // ==================== FILTERED LISTS ====================
    
    @GetMapping("/orders/status/{status}")
    public ResponseEntity<List<OrderResponse>> getOrdersByStatus(@PathVariable String status) {
        List<OrderResponse> orders = dashboardService.getOrdersByStatus(status);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/services/status/{status}")
    public ResponseEntity<List<ServiceResponse>> getServicesByStatus(@PathVariable String status) {
        List<ServiceResponse> services = dashboardService.getServicesByStatus(status);
        return new ResponseEntity<>(services, HttpStatus.OK);
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable String role) {
        List<UserResponse> users = dashboardService.getUsersByRole(role);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/transfer-requests/status/{status}")
    public ResponseEntity<List<TransferResponse>> getTransferRequestsByStatus(@PathVariable String status) {
        List<TransferResponse> transfers = dashboardService.getTransferRequestsByStatus(status);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    // ==================== RECENT ACTIVITY ====================
    
    @GetMapping("/recent/orders")
    public ResponseEntity<List<OrderResponse>> getRecentOrders(@RequestParam(defaultValue = "10") int limit) {
        List<OrderResponse> orders = dashboardService.getRecentOrders(limit);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/recent/order-actions")
    public ResponseEntity<List<OrderActionResponse>> getRecentOrderActions(@RequestParam(defaultValue = "10") int limit) {
        List<OrderActionResponse> actions = dashboardService.getRecentOrderActions(limit);
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }

    @GetMapping("/recent/user-actions")
    public ResponseEntity<List<UserActionsResponse>> getRecentUserActions(@RequestParam(defaultValue = "10") int limit) {
        List<UserActionsResponse> actions = dashboardService.getRecentUserActions(limit);
        return new ResponseEntity<>(actions, HttpStatus.OK);
    }

    @GetMapping("/recent/transfer-requests")
    public ResponseEntity<List<TransferResponse>> getRecentTransferRequests(@RequestParam(defaultValue = "10") int limit) {
        List<TransferResponse> transfers = dashboardService.getRecentTransferRequests(limit);
        return new ResponseEntity<>(transfers, HttpStatus.OK);
    }

    // ==================== ANALYTICS & STATISTICS ====================
    
    @GetMapping("/analytics/orders-by-service")
    public ResponseEntity<List<Object[]>> getOrdersByServiceStats() {
        List<Object[]> stats = dashboardService.getOrdersByServiceStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/analytics/orders-by-window")
    public ResponseEntity<List<Object[]>> getOrdersByWindowStats() {
        List<Object[]> stats = dashboardService.getOrdersByWindowStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    @GetMapping("/analytics/orders-by-date")
    public ResponseEntity<List<Object[]>> getOrdersByDateStats(@RequestParam(defaultValue = "7") int days) {
        List<Object[]> stats = dashboardService.getOrdersByDateStats(days);
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }

    // ==================== QUEUE MANAGEMENT ====================
    
    @GetMapping("/queue/current")
    public ResponseEntity<List<OrderResponse>> getCurrentQueue() {
        List<OrderResponse> queue = dashboardService.getCurrentQueue();
        return new ResponseEntity<>(queue, HttpStatus.OK);
    }

    @GetMapping("/queue/pending")
    public ResponseEntity<List<OrderResponse>> getPendingOrders() {
        List<OrderResponse> orders = dashboardService.getPendingOrders();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/queue/in-progress")
    public ResponseEntity<List<OrderResponse>> getOrdersInProgress() {
        List<OrderResponse> orders = dashboardService.getOrdersInProgress();
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    // ==================== ENTITY COUNTS ====================
    
    @GetMapping("/count/categories")
    public ResponseEntity<Long> getCategoriesCount() {
        long count = dashboardService.getAllCategories().size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/count/services")
    public ResponseEntity<Long> getServicesCount() {
        long count = dashboardService.getAllServices().size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/count/windows")
    public ResponseEntity<Long> getWindowsCount() {
        long count = dashboardService.getAllWindows().size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/count/users")
    public ResponseEntity<Long> getUsersCount() {
        long count = dashboardService.getAllUsers().size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/count/orders")
    public ResponseEntity<Long> getOrdersCount() {
        long count = dashboardService.getAllOrders().size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    @GetMapping("/count/transfer-requests")
    public ResponseEntity<Long> getTransferRequestsCount() {
        long count = dashboardService.getAllTransferRequests().size();
        return new ResponseEntity<>(count, HttpStatus.OK);
    }

    // ==================== HEALTH CHECK ====================
    
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return new ResponseEntity<>("Dashboard API is running", HttpStatus.OK);
    }

    // ==================== DATA VERIFICATION ====================
    
    @GetMapping("/data-status")
    public ResponseEntity<Object> getDataStatus() {
        Map<String, Object> dataStatus = new HashMap<>();
        
        // Count all entities
        dataStatus.put("categoriesCount", dashboardService.getAllCategories().size());
        dataStatus.put("servicesCount", dashboardService.getAllServices().size());
        dataStatus.put("windowsCount", dashboardService.getAllWindows().size());
        dataStatus.put("usersCount", dashboardService.getAllUsers().size());
        dataStatus.put("ordersCount", dashboardService.getAllOrders().size());
        dataStatus.put("transferRequestsCount", dashboardService.getAllTransferRequests().size());
        dataStatus.put("orderActionsCount", dashboardService.getAllOrderActions().size());
        dataStatus.put("userActionsCount", dashboardService.getAllUserActions().size());
        
        // Recent activity counts
        dataStatus.put("recentOrdersCount", dashboardService.getRecentOrders(50).size());
        dataStatus.put("pendingOrdersCount", dashboardService.getPendingOrders().size());
        dataStatus.put("currentQueueCount", dashboardService.getCurrentQueue().size());
        
        dataStatus.put("status", "Rich data loaded successfully");
        dataStatus.put("timestamp", java.time.LocalDateTime.now());
        
        return new ResponseEntity<>(dataStatus, HttpStatus.OK);
    }
}
