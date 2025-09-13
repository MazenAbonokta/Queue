package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.Role;
import com.dlj4.tech.queue.constants.ServiceStatus;
import com.dlj4.tech.queue.constants.ServiceType;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataInitializationService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ServiceRepository serviceRepository;
    private final UserActionRepository userActionRepository;
    private final OrderActionsRepository orderActionsRepository;

    @Bean
    @Profile("!test") // Don't run in test environment
    public ApplicationRunner initializeAdditionalData() {
        return args -> {
            try {
                log.info("Starting dashboard data initialization...");
                
                // Add more recent activity if data is sparse
                addRecentActivity();
                
                // Add more user actions for better analytics
                addUserActivityLogs();
                
                log.info("Dashboard data initialization completed successfully!");
                
            } catch (Exception e) {
                log.warn("Could not initialize additional dashboard data: {}", e.getMessage());
                // Don't fail startup if data initialization fails
            }
        };
    }

    private void addRecentActivity() {
        Random random = new Random();
        ZonedDateTime now = ZonedDateTime.now();
        
        // Add recent user actions if table is empty or sparse
        long userActionCount = userActionRepository.count();
        if (userActionCount < 20) {
            log.info("Adding recent user activity data...");
            
            List<User> users = userRepository.findAll();
            String[] activities = {"LOGIN", "LOGOUT"};
            
            for (int i = 0; i < 15; i++) {
                if (!users.isEmpty()) {
                    User randomUser = users.get(random.nextInt(users.size()));
                    String randomActivity = activities[random.nextInt(activities.length)];
                    
                    UserActions userAction = UserActions.builder()
                        .createdAt(now.minusMinutes(random.nextInt(120))) // Last 2 hours
                        .username(randomUser.getUsername())
                        .userStatus(com.dlj4.tech.queue.constants.UserStatus.valueOf(randomActivity))
                        .build();
                    
                    try {
                        userActionRepository.save(userAction);
                    } catch (Exception e) {
                        log.debug("Could not save user action: {}", e.getMessage());
                    }
                }
            }
        }
        
        // Add recent order actions if sparse
        long orderActionCount = orderActionsRepository.count();
        if (orderActionCount < 30) {
            log.info("Adding recent order action data...");
            
            List<Order> recentOrders = orderRepository.findAll().stream()
                .filter(order -> order.getCreatedAt() != null && 
                               order.getCreatedAt().isAfter(now.minusHours(6)))
                .toList();
            
            String[] orderStatuses = {"PENDING", "CALLED", "BOOKED", "CANCELLED"};
            
            for (Order order : recentOrders) {
                // Add 1-3 status changes per order
                int statusChanges = 1 + random.nextInt(3);
                ZonedDateTime orderTime = order.getCreatedAt();
                
                for (int i = 0; i < statusChanges; i++) {
                    String status = orderStatuses[Math.min(i, orderStatuses.length - 1)];
                    
                    OrderAction orderAction = OrderAction.builder()
                        .createdAt(orderTime.plusMinutes(i * 10 + random.nextInt(10)))
                        .orderStatus(status)
                        .order(order)
                        .build();
                    
                    try {
                        orderActionsRepository.save(orderAction);
                    } catch (Exception e) {
                        log.debug("Could not save order action: {}", e.getMessage());
                    }
                }
            }
        }
    }
    
    private void addUserActivityLogs() {
        Random random = new Random();
        ZonedDateTime now = ZonedDateTime.now();
        
        // Add activity logs for the last week for better trend analysis
        List<User> operators = userRepository.findAll().stream()
            .filter(user -> user.getRole() == Role.USER)
            .toList();
        
        if (!operators.isEmpty()) {
            log.info("Adding historical user activity logs...");
            
            String[] operatorActivities = {"LOGIN", "LOGOUT"};
            
            // Add activities for last 7 days
            for (int day = 1; day <= 7; day++) {
                ZonedDateTime dayStart = now.minusDays(day).withHour(8).withMinute(0);
                ZonedDateTime dayEnd = now.minusDays(day).withHour(17).withMinute(0);
                
                for (User operator : operators) {
                    // Each operator has 5-15 activities per day
                    int activitiesPerDay = 5 + random.nextInt(11);
                    
                    for (int i = 0; i < activitiesPerDay; i++) {
                        String activity = operatorActivities[random.nextInt(operatorActivities.length)];
                        
                        // Random time during work hours
                        long minutesInDay = dayEnd.toEpochSecond() - dayStart.toEpochSecond();
                        ZonedDateTime activityTime = dayStart.plusSeconds(random.nextLong() % minutesInDay);
                        
                        UserActions userAction = UserActions.builder()
                            .createdAt(activityTime)
                            .username(operator.getUsername())
                            .userStatus(com.dlj4.tech.queue.constants.UserStatus.valueOf(activity))
                            .build();
                        
                        try {
                            userActionRepository.save(userAction);
                        } catch (Exception e) {
                            log.debug("Could not save historical user action: {}", e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
