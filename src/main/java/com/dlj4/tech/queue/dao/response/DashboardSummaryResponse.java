package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardSummaryResponse {
    
    // Date Information
    private String date; // Date in YYYY-MM-DD format
    private String dayName; // Day name (Monday, Tuesday, etc.)
    
    // General Statistics
    private Long totalOrders;
    private Long todayOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Long cancelledOrders;
    private Long inProgressOrders;
    
    // Service Statistics
    private Long totalServices;
    private Long activeServices;
    private Long inactiveServices;
    
    // Window Statistics
    private Long totalWindows;
    private Long activeWindows;
    
    // User Statistics
    private Long totalUsers;
    private Long activeUsers;
    private Long operatorUsers;
    private Long customerUsers;
    
    // Category Statistics
    private Long totalCategories;
    
    // Transfer Statistics
    private Long totalTransferRequests;
    private Long pendingTransferRequests;
    private Long approvedTransferRequests;
    private Long rejectedTransferRequests;
    
    // Queue Statistics
    private Double averageWaitTime;
    private Long longestWaitTime;
    private Long shortestWaitTime;
}
