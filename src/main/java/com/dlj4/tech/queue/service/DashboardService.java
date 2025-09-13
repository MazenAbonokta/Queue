package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.response.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DashboardService {
    
    // Summary Statistics
    List<DashboardSummaryResponse> getDashboardSummary();
    
    // Entity Lists
    List<CategoryResponse> getAllCategories();
    List<ServiceResponse> getAllServices();
    List<WindowResponse> getAllWindows();
    List<UserResponse> getAllUsers();
    List<OrderResponse> getAllOrders();
    List<TransferResponse> getAllTransferRequests();
    List<WindowRoleResponse> getAllWindowRoles();
    List<OrderActionResponse> getAllOrderActions();
    List<OrderArchiveResponse> getAllOrderArchives();
    List<UserActionsResponse> getAllUserActions();
    
    // Filtered Lists
    List<OrderResponse> getOrdersByStatus(String status);
    List<ServiceResponse> getServicesByStatus(String status);
    List<UserResponse> getUsersByRole(String role);
    List<TransferResponse> getTransferRequestsByStatus(String status);
    
    // Recent Activity
    List<OrderResponse> getRecentOrders(int limit);
    List<OrderActionResponse> getRecentOrderActions(int limit);
    List<UserActionsResponse> getRecentUserActions(int limit);
    List<TransferResponse> getRecentTransferRequests(int limit);
    
    // Analytics
    List<Object[]> getOrdersByServiceStats();
    List<Object[]> getOrdersByWindowStats();
    List<Object[]> getOrdersByDateStats(int days);
    
    // Queue Management
    List<OrderResponse> getCurrentQueue();
    List<OrderResponse> getPendingOrders();
    List<OrderResponse> getOrdersInProgress();
}
