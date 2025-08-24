package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.Role;
import com.dlj4.tech.queue.constants.ServiceStatus;
import com.dlj4.tech.queue.constants.TransferRequestStatus;
import com.dlj4.tech.queue.dao.response.*;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.*;
import com.dlj4.tech.queue.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;
    
    @Autowired
    private WindowRepository windowRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private TransferRequestRepository transferRequestRepository;
    
    @Autowired
    private WindowRoleRepository windowRoleRepository;
    
    @Autowired
    private OrderActionsRepository orderActionsRepository;
    
    @Autowired
    private UserActionRepository userActionRepository;
    
    @Autowired
    private ObjectsDataMapper objectsDataMapper;

    @Override
    public DashboardSummaryResponse getDashboardSummary() {
        // Count total orders
        long totalOrders = orderRepository.count();
        
        // Count today's orders
        ZonedDateTime startOfDay = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().getZone());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);
        long todayOrders = orderRepository.findByTodayAndCreatedAtBefore(true, endOfDay).size();
        
        // Count orders by status
        List<Order> allOrders = orderRepository.findAll();
        long pendingOrders = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
        long bookedOrders = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CALLED).count();
        long calledOrders = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CALLED).count();
        long cancelledOrders = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED).count();
        // long transferOrders = allOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.TRANSFER).count();
        
        // Count services
        long totalServices = serviceRepository.count();
        long activeServices = serviceRepository.findAllByServiceStatusAndServiceType(
            ServiceStatus.ACTIVE, null).size();
        long inactiveServices = totalServices - activeServices;
        
        // Count windows
        long totalWindows = windowRepository.count();
        
        // Count active windows (windows with active orders or users)
        long activeWindows = windowRepository.findAll().stream()
            .filter(window -> !window.getOrders().isEmpty() || !window.getUsers().isEmpty())
            .count();
        
        // Count users
        long totalUsers = userRepository.count();
        
        // Count active users (users with LOGIN status)
        long activeUsers = userRepository.findAll().stream()
            .filter(user -> "LOGIN".equals(user.getStatus()))
            .count();
        
        // Count users by role
        long operatorUsers = userRepository.findAll().stream()
            .filter(user -> user.getRole() == Role.USER)
            .count();
        
        long customerUsers = userRepository.findAll().stream()
            .filter(user -> user.getRole() == Role.ADMIN)
            .count();
        
        // Count categories
        long totalCategories = categoryRepository.count();
        
        // Count transfer requests
        long totalTransferRequests = transferRequestRepository.count();
        
        // Count transfer requests by status
        long pendingTransferRequests = transferRequestRepository.findAll().stream()
            .filter(tr -> tr.getRequestStatus() == TransferRequestStatus.SEND)
            .count();
        
        long approvedTransferRequests = transferRequestRepository.findAll().stream()
            .filter(tr -> tr.getRequestStatus() == TransferRequestStatus.APPROVED)
            .count();
        
        long rejectedTransferRequests = transferRequestRepository.findAll().stream()
            .filter(tr -> tr.getRequestStatus() == TransferRequestStatus.REJECTED)
            .count();
        
        // Calculate wait time statistics from OrderActions
        List<OrderAction> allOrderActions = orderActionsRepository.findAll();
        
        Double averageWaitTime = null;
        Long longestWaitTime = null;
        Long shortestWaitTime = null;
        
        // Group order actions by order to find PENDING and CALLED timestamps
        Map<Long, List<OrderAction>> orderActionsByOrderId = allOrderActions.stream()
            .collect(Collectors.groupingBy(action -> action.getOrder().getId()));
        
        List<Long> waitTimes = new ArrayList<>();
        
        for (List<OrderAction> actions : orderActionsByOrderId.values()) {
            // Find PENDING action
            Optional<OrderAction> pendingAction = actions.stream()
                .filter(action -> OrderStatus.PENDING.name().equals(action.getOrderStatus()))
                .findFirst();
            
            // Find CALLED action
            Optional<OrderAction> calledAction = actions.stream()
                .filter(action -> OrderStatus.CALLED.name().equals(action.getOrderStatus()))
                .findFirst();
            
            // Calculate wait time if both actions exist
            if (pendingAction.isPresent() && calledAction.isPresent()) {
                long waitTimeMillis = calledAction.get().getCreatedAt().toInstant().toEpochMilli() - 
                                    pendingAction.get().getCreatedAt().toInstant().toEpochMilli();
                long waitTimeSeconds = waitTimeMillis / 1000; // Convert to seconds
                waitTimes.add(waitTimeSeconds);
            }
        }
        
        if (!waitTimes.isEmpty()) {
            averageWaitTime = waitTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
            
            longestWaitTime = waitTimes.stream()
                .mapToLong(Long::longValue)
                .max()
                .orElse(0L);
            
            shortestWaitTime = waitTimes.stream()
                .mapToLong(Long::longValue)
                .min()
                .orElse(0L);
        }
        
        return DashboardSummaryResponse.builder()
            .totalOrders(totalOrders)
            .todayOrders(todayOrders)
            .pendingOrders(pendingOrders)
            .completedOrders(bookedOrders)
            .cancelledOrders(cancelledOrders)
            .inProgressOrders(calledOrders)
            .totalServices(totalServices)
            .activeServices(activeServices)
            .inactiveServices(inactiveServices)
            .totalWindows(totalWindows)
            .activeWindows(activeWindows)
            .totalUsers(totalUsers)
            .activeUsers(activeUsers)
            .operatorUsers(operatorUsers)
            .customerUsers(customerUsers)
            .totalCategories(totalCategories)
            .totalTransferRequests(totalTransferRequests)
            .pendingTransferRequests(pendingTransferRequests)
            .approvedTransferRequests(approvedTransferRequests)
            .rejectedTransferRequests(rejectedTransferRequests)
            .averageWaitTime(averageWaitTime)
            .longestWaitTime(longestWaitTime)
            .shortestWaitTime(shortestWaitTime)
            .build();
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(objectsDataMapper::categoryToCategoryResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getAllServices() {
        return serviceRepository.findAll().stream()
            .map(objectsDataMapper::ServiceToServiceResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<WindowResponse> getAllWindows() {
        return windowRepository.findAll().stream()
            .map(objectsDataMapper::windowToWindowResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
            .map(objectsDataMapper::userToUserResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
            .map(objectsDataMapper::orderToOrderResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TransferResponse> getAllTransferRequests() {
        return transferRequestRepository.findAll().stream()
            .map(this::mapTransferRequestToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<WindowRoleResponse> getAllWindowRoles() {
        return windowRoleRepository.findAll().stream()
            .map(this::mapWindowRoleToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderActionResponse> getAllOrderActions() {
        return orderActionsRepository.findAllWithOrderDetails().stream()
            .map(this::mapOrderActionToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderArchiveResponse> getAllOrderArchives() {
        // This would require an OrderArchiveRepository
        // For now, return empty list
        return List.of();
    }

    @Override
    public List<UserActionsResponse> getAllUserActions() {
        return userActionRepository.findAll().stream()
            .map(this::mapUserActionToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getOrdersByStatus(String status) {
        OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
        return orderRepository.findAll().stream()
            .filter(order -> order.getOrderStatus() == orderStatus)
            .map(objectsDataMapper::orderToOrderResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceResponse> getServicesByStatus(String status) {
        ServiceStatus serviceStatus = ServiceStatus.valueOf(status.toUpperCase());
        return serviceRepository.findAll().stream()
            .filter(service -> service.getServiceStatus() == serviceStatus)
            .map(objectsDataMapper::ServiceToServiceResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(String role) {
        Role userRole = Role.valueOf(role.toUpperCase());
        return userRepository.findAll().stream()
            .filter(user -> user.getRole() == userRole)
            .map(objectsDataMapper::userToUserResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TransferResponse> getTransferRequestsByStatus(String status) {
        TransferRequestStatus transferStatus = TransferRequestStatus.valueOf(status.toUpperCase());
        return transferRequestRepository.findAll().stream()
            .filter(transfer -> transfer.getRequestStatus() == transferStatus)
            .map(this::mapTransferRequestToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getRecentOrders(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable).getContent().stream()
            .map(objectsDataMapper::orderToOrderResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderActionResponse> getRecentOrderActions(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return orderActionsRepository.findRecentWithOrderDetails(pageable).stream()
            .map(this::mapOrderActionToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<UserActionsResponse> getRecentUserActions(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return userActionRepository.findAll(pageable).getContent().stream()
            .map(this::mapUserActionToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<TransferResponse> getRecentTransferRequests(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return transferRequestRepository.findAll(pageable).getContent().stream()
            .map(this::mapTransferRequestToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<Object[]> getOrdersByServiceStats() {
        // This would require custom query implementation
        return List.of();
    }

    @Override
    public List<Object[]> getOrdersByWindowStats() {
        // This would require custom query implementation
        return List.of();
    }

    @Override
    public List<Object[]> getOrdersByDateStats(int days) {
        // This would require custom query implementation
        return List.of();
    }

    @Override
    public List<OrderResponse> getCurrentQueue() {
        return orderRepository.findAll().stream()
            .filter(order -> order.getOrderStatus() == OrderStatus.PENDING || 
                           order.getOrderStatus() == OrderStatus.CALLED)
            .map(objectsDataMapper::orderToOrderResponse)
            .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getPendingOrders() {
        return getOrdersByStatus("PENDING");
    }

    @Override
    public List<OrderResponse> getOrdersInProgress() {
        return getOrdersByStatus("TRANSFER");
    }

    // Helper methods for mapping entities to responses
    private WindowRoleResponse mapWindowRoleToResponse(WindowRole windowRole) {
        return WindowRoleResponse.builder()
            .id(windowRole.getId())
            .windowId(windowRole.getWindow().getId())
            .windowNumber(windowRole.getWindow().getWindowNumber())
            .serviceId(windowRole.getService().getId())
            .serviceName(windowRole.getService().getName())
            .serviceCode(windowRole.getService().getCode())
            .categoryName(windowRole.getService().getCategory().getName())
            .build();
    }

    private OrderActionResponse mapOrderActionToResponse(OrderAction orderAction) {
        // Handle null order case
        if (orderAction.getOrder() == null) {
            return OrderActionResponse.builder()
                .id(orderAction.getId())
                .createdAt(orderAction.getCreatedAt())
                .orderStatus(orderAction.getOrderStatus())
                .orderId(null)
                .orderCode("N/A")
                .serviceName("Unknown Service")
                .windowNumber("N/A")
                .build();
        }

        Order order = orderAction.getOrder();
        return OrderActionResponse.builder()
            .id(orderAction.getId())
            .createdAt(orderAction.getCreatedAt())
            .orderStatus(orderAction.getOrderStatus())
            .orderId(order.getId())
            .orderCode(order.getCode() != null ? order.getCode() : "N/A")
            .serviceName(order.getService() != null ? order.getService().getName() : "Unknown Service")
            .windowNumber(order.getWindow() != null ? order.getWindow().getWindowNumber() : "N/A")
            .build();
    }

    private UserActionsResponse mapUserActionToResponse(UserActions userAction) {
        return UserActionsResponse.builder()
            .id(userAction.getId())
            .createdAt(userAction.getCreatedAt())
            .userStatus(userAction.getUserStatus())
            .username(userAction.getUsername())
            .build();
    }

    private TransferResponse mapTransferRequestToResponse(TransferRequest transferRequest) {
        return TransferResponse.builder()
            .requestId(transferRequest.getId())
            .order(transferRequest.getOrder() != null ? transferRequest.getOrder().getCode() : "N/A")
            .userRequester(transferRequest.getRequestUser() != null ? transferRequest.getRequestUser().getUsername() : "N/A")
            .requestDate(transferRequest.getCreatedAt() != null ? transferRequest.getCreatedAt().toString() : "N/A")
            .requestedService(transferRequest.getRequestService() != null ? transferRequest.getRequestService().getName() : "N/A")
            .requestedWindow(transferRequest.getRequestWindow() != null ? transferRequest.getRequestWindow().getWindowNumber() : "N/A")
            .targetService(transferRequest.getResponseService() != null ? transferRequest.getResponseService().getName() : "N/A")
            .userResponse(transferRequest.getResponseUser() != null ? transferRequest.getResponseUser().getUsername() : "N/A")
            .targetWindow(transferRequest.getResponseWindow() != null ? transferRequest.getResponseWindow().getWindowNumber() : "N/A")
            .responseDate(transferRequest.getUpdatedAt() != null ? transferRequest.getUpdatedAt().toString() : "N/A")
            .status(transferRequest.getRequestStatus())
            .build();
    }
}
