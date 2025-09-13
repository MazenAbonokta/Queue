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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public List<DashboardSummaryResponse> getDashboardSummary() {
        List<DashboardSummaryResponse> dailySummaries = new ArrayList<>();
        
        // Get current date and calculate the start date (last 30 days by default)
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.minusDays(29); // Include today and 29 previous days
        
        // Generate daily summaries
        for (LocalDate date = startDate; !date.isAfter(currentDate); date = date.plusDays(1)) {
            ZonedDateTime startOfDay = date.atStartOfDay(ZonedDateTime.now().getZone());
            ZonedDateTime endOfDay = startOfDay.plusDays(1);
            
            // Get orders for this specific day
            List<Order> dayOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && 
                               order.getCreatedAt().isAfter(startOfDay) && 
                               order.getCreatedAt().isBefore(endOfDay))
                .collect(Collectors.toList());
            
            // Count orders by status for this day
            long dayTotalOrders = dayOrders.size();
            long dayPendingOrders = dayOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.PENDING).count();
            long dayCompletedOrders = dayOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CALLED).count();
            long dayCancelledOrders = dayOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.CANCELLED).count();
            long dayInProgressOrders = dayOrders.stream().filter(o -> o.getOrderStatus() == OrderStatus.BOOKED).count();
            
            // Get order actions for this day
            List<OrderAction> dayOrderActions = orderActionsRepository.findAll().stream()
                .filter(action -> action.getCreatedAt() != null && 
                                action.getCreatedAt().isAfter(startOfDay) && 
                                action.getCreatedAt().isBefore(endOfDay))
                .collect(Collectors.toList());
            
            // Calculate wait time statistics for this day
            Double averageWaitTime = null;
            Long dayLongestWaitTime = null;
            Long dayShortestWaitTime = null;
            
            // Group order actions by order to find PENDING and CALLED timestamps for this day
            Map<Long, List<OrderAction>> dayOrderActionsByOrderId = dayOrderActions.stream()
                .collect(Collectors.groupingBy(action -> action.getOrder().getId()));
            
            List<Long> dayWaitTimes = new ArrayList<>();
            
            for (List<OrderAction> actions : dayOrderActionsByOrderId.values()) {
                // Find PENDING action
                Optional<OrderAction> pendingAction = actions.stream()
                    .filter(action -> OrderStatus.PENDING.name().equals(action.getOrderStatus()))
                    .findFirst();
                
                // Find BOOKED action
                Optional<OrderAction> bookedAction = actions.stream()
                    .filter(action -> OrderStatus.BOOKED.name().equals(action.getOrderStatus()))
                    .findFirst();
                
                // Calculate wait time if both actions exist
                if (pendingAction.isPresent() && bookedAction.isPresent()) {
                    long waitTimeMillis = bookedAction.get().getCreatedAt().toInstant().toEpochMilli() - 
                                        pendingAction.get().getCreatedAt().toInstant().toEpochMilli();
                    long waitTimeSeconds = waitTimeMillis / 1000; // Convert to seconds
                    dayWaitTimes.add(waitTimeSeconds);
                }
            }
            
            if (!dayWaitTimes.isEmpty()) {
                averageWaitTime = dayWaitTimes.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0.0);
                
                dayLongestWaitTime = dayWaitTimes.stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0L);
                
                dayShortestWaitTime = dayWaitTimes.stream()
                    .mapToLong(Long::longValue)
                    .min()
                    .orElse(0L);
            }
            
            // Get transfer requests for this day
            List<TransferRequest> dayTransferRequests = transferRequestRepository.findAll().stream()
                .filter(tr -> tr.getCreatedAt() != null && 
                             tr.getCreatedAt().isAfter(startOfDay) && 
                             tr.getCreatedAt().isBefore(endOfDay))
                .collect(Collectors.toList());
            
            long dayTotalTransferRequests = dayTransferRequests.size();
            long dayPendingTransferRequests = dayTransferRequests.stream()
                .filter(tr -> tr.getRequestStatus() == TransferRequestStatus.SEND)
                .count();
            long dayApprovedTransferRequests = dayTransferRequests.stream()
                .filter(tr -> tr.getRequestStatus() == TransferRequestStatus.APPROVED)
                .count();
            long dayRejectedTransferRequests = dayTransferRequests.stream()
                .filter(tr -> tr.getRequestStatus() == TransferRequestStatus.REJECTED)
                .count();
            
            // Get user actions for this day
            List<UserActions> dayUserActions = userActionRepository.findAll().stream()
                .filter(ua -> ua.getCreatedAt() != null && 
                             ua.getCreatedAt().isAfter(startOfDay) && 
                             ua.getCreatedAt().isBefore(endOfDay))
                .collect(Collectors.toList());
            
            // Count active users for this day (users with LOGIN status)
            long dayActiveUsers = dayUserActions.stream()
                .filter(ua -> "LOGIN".equals(ua.getUserStatus()))
                .count();
            
            // Build daily summary
            DashboardSummaryResponse dailySummary = DashboardSummaryResponse.builder()
                .date(date.format(DateTimeFormatter.ISO_LOCAL_DATE)) // YYYY-MM-DD format
                .dayName(date.getDayOfWeek().name()) // MONDAY, TUESDAY, etc.
                .totalOrders(dayTotalOrders)
                .todayOrders(dayTotalOrders) // For daily view, today's orders = total orders for that day
                .pendingOrders(dayPendingOrders)
                .completedOrders(dayCompletedOrders+dayInProgressOrders)
                .cancelledOrders(dayCancelledOrders)
                .inProgressOrders(dayInProgressOrders)
                .totalServices(serviceRepository.count()) // These are global counts
                .activeServices((long) serviceRepository.findAllByServiceStatusAndServiceType(ServiceStatus.ACTIVE, null).size())
                .inactiveServices(serviceRepository.count() - (long) serviceRepository.findAllByServiceStatusAndServiceType(ServiceStatus.ACTIVE, null).size())
                .totalWindows(windowRepository.count())
                .activeWindows(windowRepository.findAll().stream()
                    .filter(window -> !window.getOrders().isEmpty() || !window.getUsers().isEmpty())
                    .count())
                .totalUsers(userRepository.count())
                .activeUsers(dayActiveUsers)
                .operatorUsers(userRepository.findAll().stream().filter(user -> user.getRole() == Role.USER).count())
                .customerUsers(userRepository.findAll().stream().filter(user -> user.getRole() == Role.ADMIN).count())
                .totalCategories(categoryRepository.count())
                .totalTransferRequests(dayTotalTransferRequests)
                .pendingTransferRequests(dayPendingTransferRequests)
                .approvedTransferRequests(dayApprovedTransferRequests)
                .rejectedTransferRequests(dayRejectedTransferRequests)
                .averageWaitTime(averageWaitTime)
                .longestWaitTime(dayLongestWaitTime)
                .shortestWaitTime(dayShortestWaitTime)
                .build();
            
            dailySummaries.add(dailySummary);
        }
        
        return dailySummaries;
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
