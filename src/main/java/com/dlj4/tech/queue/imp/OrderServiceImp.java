package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.constants.ServiceType;
import com.dlj4.tech.queue.constants.TransferRequestStatus;
import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.request.TransferRequestDTO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.dao.response.TransferResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.dto.OrderMessageDto;
import com.dlj4.tech.queue.dto.TicketsMessage;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.*;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.TemplatePrintService;
import com.dlj4.tech.queue.service.WindowService;
import jakarta.transaction.Transactional;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.apache.logging.log4j.util.InternalException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepository;
    private final ServiceService serviceService;
    private final WindowService windowService;
    private final UserRepository userRepository;
    private final TemplatePrintService printService;
    private final ObjectsDataMapper objectsDataMapper;
    private final RabbitTemplate rabbitTemplate;
    private final TransferRequestRepository transferRequestRepository;
    private final OrderActionsRepository orderActionsRepository;
    private final NotificationService notificationService;
    private final ServiceRepository serviceRepository;
    private final String queueName;
    private Clip clip;
    
    public OrderServiceImp(
            OrderRepository orderRepository,
            ServiceService serviceService,
            WindowService windowService,
            UserRepository userRepository,
            TemplatePrintService printService,
            ObjectsDataMapper objectsDataMapper,
            RabbitTemplate rabbitTemplate,
            TransferRequestRepository transferRequestRepository,
            OrderActionsRepository orderActionsRepository,
            NotificationService notificationService,
            ServiceRepository serviceRepository,
            @Value("${queue.name}") String queueName) {
        this.orderRepository = orderRepository;
        this.serviceService = serviceService;
        this.windowService = windowService;
        this.userRepository = userRepository;
        this.printService = printService;
        this.objectsDataMapper = objectsDataMapper;
        this.rabbitTemplate = rabbitTemplate;
        this.transferRequestRepository = transferRequestRepository;
        this.orderActionsRepository = orderActionsRepository;
        this.notificationService = notificationService;
        this.serviceRepository = serviceRepository;
        this.queueName = queueName;
    }

    /**
     * Creates a new order for the specified service
     *
     * @param serviceId The ID of the service for which to create an order
     * @throws BadRequestException If the service number has reached its maximum limit or if serviceId is invalid
     */
    @Override
    public void createOrder(Long serviceId) throws BadRequestException {
        // Validate input
        if (serviceId == null) {
            log.error("Service ID cannot be null");
            throw new BadRequestException("Service ID cannot be null");
        }
        
        // Get service details
        ServiceEntity fetchedService;
        try {
            fetchedService = serviceService.getServiceById(serviceId);
            if (fetchedService == null) {
                log.error("Service with ID {} not found", serviceId);
                throw new BadRequestException("Service with ID " + serviceId + " not found");
            }
        } catch (Exception e) {
            log.error("Failed to retrieve service with ID {}: {}", serviceId, e.getMessage());
            throw new BadRequestException("Invalid service ID: " + serviceId);
        }
        
        // Find the current maximum number for this service
        Long currentMaxNumber = orderRepository.findMaxCurrentNumberByServiceId(
                serviceId, 
                fetchedService.getCode()
        );
        
        // Check if we've reached the maximum number for this service
        if (currentMaxNumber == null || currentMaxNumber > fetchedService.getEnd()) {
            log.error("Service Number Is Max for service: {}", fetchedService.getName());
            throw new BadRequestException("Service {" + fetchedService.getName() + "} Number Is Max");
        }
        
        // Calculate the new number
        Long newCurrentNumber = currentMaxNumber == 0 ? 
                fetchedService.getStart() : 
                (currentMaxNumber + 1);

        // Create and save the order
        Order order = objectsDataMapper.createOrderEntity(null, fetchedService, newCurrentNumber);
        order = orderRepository.save(order);
        
        // Generate code for the ticket
        String code = order.getService().getCode() + 
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Update ticket count via notification service
        updateServiceTicketCount(serviceId);
        
        // Print the ticket
        printService.printTicket(
                "Ticket", 
                code, 
                order.getService().getName(), 
                "44", 
                order.getCurrentNumber().toString(), 
                "Test"
        );
    }

    /**
     * Calls a number to a specific screen/window and plays audio notification
     *
     * @param number The ticket number to call
     * @param screenNumber The screen/window number where the ticket is called
     * @param code The service code
     * @param ipAddress The IP address of the display device
     * @throws IllegalArgumentException If any of the required parameters are invalid
     */
    @Override
    public void callNumber(Long number, String screenNumber, String code, String ipAddress) {
        // Validate input parameters
        if (number == null) {
            log.error("Ticket number cannot be null");
            throw new IllegalArgumentException("Ticket number cannot be null");
        }
        
        if (screenNumber == null || screenNumber.trim().isEmpty()) {
            log.error("Screen number cannot be null or empty");
            throw new IllegalArgumentException("Screen number cannot be null or empty");
        }
        
        if (code == null || code.trim().isEmpty()) {
            log.error("Service code cannot be null or empty");
            throw new IllegalArgumentException("Service code cannot be null or empty");
        }
        
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            log.error("IP address cannot be null or empty");
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        
        // Prepare the list of sound files to play in sequence
        List<String> soundFiles = Arrays.asList(
                "card.mp3",
                "number_" + number + ".mp3",
                "window.mp3", 
                "number_" + screenNumber + ".mp3"
        );
        
        String ticketIdentifier = code + "-" + number;
        
        try {
            // Send the number to Arduino display
            sendToArduino(ticketIdentifier, ipAddress);
            
            // Update the main screen via notification service
            notificationService.sendNewTicketToMainScreen(
                    MainScreenTicket.builder()
                            .ticketNumber(ticketIdentifier)
                            .counter(screenNumber)
                            .build()
            );
            log.info("Sent ticket {} to main screen on counter {}", ticketIdentifier, screenNumber);
        } catch (Exception e) {
            log.error("Failed to send ticket to display: {}", e.getMessage());
            e.printStackTrace();
        }
        
        // Play the sequence of audio files
        for (String fileName : soundFiles) {
            playSound(fileName);
        }
    }

    /**
     * Sends a ticket number to the queue for processing
     *
     * @param Number The ticket number to send
     * @param ScreenNumber The screen/window number
     * @param Code The service code
     * @param IpAddress The IP address of the display device
     * @throws IllegalArgumentException If any of the required parameters are invalid
     */
    @Override
    public void SendNumberToQueue(Long Number, String ScreenNumber, String Code, String IpAddress) {
        // Validate input parameters
        if (Number == null) {
            log.error("Ticket number cannot be null");
            throw new IllegalArgumentException("Ticket number cannot be null");
        }
        
        if (ScreenNumber == null || ScreenNumber.trim().isEmpty()) {
            log.error("Screen number cannot be null or empty");
            throw new IllegalArgumentException("Screen number cannot be null or empty");
        }
        
        if (Code == null || Code.trim().isEmpty()) {
            log.error("Service code cannot be null or empty");
            throw new IllegalArgumentException("Service code cannot be null or empty");
        }
        
        if (IpAddress == null || IpAddress.trim().isEmpty()) {
            log.error("IP address cannot be null or empty");
            throw new IllegalArgumentException("IP address cannot be null or empty");
        }
        
        log.info("Sending ticket number {} to queue for calling", Number);

        OrderMessageDto orderMessageDto = new OrderMessageDto(Number, ScreenNumber, Code, IpAddress);
        rabbitTemplate.convertAndSend(queueName, orderMessageDto);
        
        log.info("Ticket number {} successfully sent to queue", Number);
    }

    /**
     * Fetches the next order in the queue for processing
     *
     * @param orderDAO Data object containing service ID and current order information
     * @return UserOrders object containing the next order and service information
     * @throws IllegalArgumentException If orderDAO is null or has invalid service ID
     * @throws ResourceNotFoundException If no pending order is found for the service
     */
    @Override
    public UserOrders fetchNextOrder(OrderDAO orderDAO) {
        // Validate input
        if (orderDAO == null) {
            log.error("Order DAO cannot be null");
            throw new IllegalArgumentException("Order DAO cannot be null");
        }
        
        if (orderDAO.getServiceId() == null) {
            log.error("Service ID cannot be null");
            throw new IllegalArgumentException("Service ID cannot be null");
        }
        
        // Update current order status if provided
        try {
            updateCurrentOrder(orderDAO);
        } catch (Exception e) {
            log.info("Failed to update current order: {}", e.getMessage());
        }
        
        // Get current user and their window
        User user = getCurrentUser();
        if (user == null || user.getWindow() == null) {
            log.error("Current user or window not found");
            throw new IllegalStateException("Current user or window not found");
        }
        
        Window window = windowService.getWindowByID(user.getWindow().getId());
        if (window == null) {
            log.error("Window with ID {} not found", user.getWindow().getId());
            throw new ResourceNotFoundException("Window not found");
        }
        
        // Find the next pending order for the service
        Order nextOrder = orderRepository.findFirstByOrderStatusAndServiceId(
                OrderStatus.PENDING, 
                orderDAO.getServiceId()
        );
        
        if (nextOrder == null) {
            log.error("No pending orders found for service ID {}", orderDAO.getServiceId());
            throw new ResourceNotFoundException("No pending orders found for service ID " + orderDAO.getServiceId());
        }
        
        // Update the order status to BOOKED
        nextOrder.setOrderStatus(OrderStatus.BOOKED);
        nextOrder.setCallDate(ZonedDateTime.now(ZoneId.of("UTC")));
        nextOrder.setWindow(window);
        nextOrder.setUser(user);
        orderRepository.save(nextOrder);
        
        // Create order action record
        createOrderActions(nextOrder, OrderStatus.BOOKED);

        // Send the number to the queue for display
        SendNumberToQueue(
                nextOrder.getCurrentNumber(),
                user.getWindow().getWindowNumber(),
                nextOrder.getCode(),
                window.getIpAddress()
        );

        // Map entities to response objects
        OrderResponse orderResponse = objectsDataMapper.orderToOrderResponse(nextOrder);
        ServiceEntity service = serviceService.getServiceById(orderDAO.getServiceId());
        ServiceResponse serviceResponse = objectsDataMapper.ServiceToServiceResponse(service);

        // Update ticket count via notification service
        updateServiceTicketCount(orderDAO.getServiceId());
        
        // Build and return the response
        return UserOrders.builder()
                .currentOrder(orderResponse)
                .serviceResponse(serviceResponse)
                .build();
    }

    /**
     * Updates the status of an order
     *
     * @param order The order to update
     * @param orderStatus The new status to set
     * @throws IllegalArgumentException If order or orderStatus is null
     */
    @Override
    public void updateOrderStatus(Order order, OrderStatus orderStatus) {
        // Validate input parameters
        if (order == null) {
            log.error("Order cannot be null");
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        if (orderStatus == null) {
            log.error("Order status cannot be null");
            throw new IllegalArgumentException("Order status cannot be null");
        }
        
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            log.error("Current user not found");
            throw new IllegalStateException("Current user not found");
        }
        
        order.setOrderStatus(orderStatus);
        order.setUser(currentUser);

        orderRepository.save(order);
        createOrderActions(order, orderStatus);
        
        log.info("Updated order {} status to {}", order.getId(), orderStatus);
    }

    /**
     * Updates the current order status if an order ID is provided in the DAO
     *
     * @param orderDAO Data object containing order ID and status information
     */
    private void updateCurrentOrder(OrderDAO orderDAO) {
        if (orderDAO.getOrderId() != 0) {
            try {
                Order currentOrder = getOrderById(orderDAO.getOrderId());
                if (currentOrder != null) {
                    updateOrderStatus(currentOrder, orderDAO.getOrderStatus());
                }
            } catch (ResourceNotFoundException e) {
                log.error("Failed to update current order: {}", e.getMessage());
            }
        }
    }
    /**
     * Retrieves an order by its ID
     *
     * @param orderId The ID of the order to retrieve
     * @return The order with the specified ID
     * @throws ResourceNotFoundException if the order does not exist
     */
    @Override
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + orderId + " does not exist"));
    }

    /**
     * Helper method to get user services and build user orders response
     *
     * @param userId The ID of the user
     * @param orders List of orders to process
     * @return List of UserOrders containing order and service information
     * @throws ResourceNotFoundException if the user does not exist
     */
    private List<UserOrders> buildUserOrdersResponse(Long userId, List<Order> orders) {
        // Get user and their assigned services
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " does not exist"));
        
        // Get all services assigned to the user's window
        List<ServiceEntity> serviceEntities = user.getWindow().getWindowRoles().stream()
                .map(windowRole -> windowRole.getService())
                .collect(Collectors.toList());
        
        // Map orders to response objects
        List<OrderResponse> orderResponses = orders.stream()
                .map(orderObj -> objectsDataMapper.orderToOrderResponse(orderObj))
                .collect(Collectors.toList());
        
        // Create a map of service ID to order response for quick lookup
        Map<Long, OrderResponse> orderResponseMap = orderResponses.stream()
                .collect(Collectors.toMap(
                    OrderResponse::getServiceId, 
                    orderResponse -> orderResponse,
                    (existing, replacement) -> existing // In case of duplicate keys, keep the existing one
                ));
        
        // Build the final response list
        List<UserOrders> userOrders = new ArrayList<>();
        for (ServiceEntity service : serviceEntities) {
            OrderResponse orderResponse = orderResponseMap.get(service.getId());
            userOrders.add(UserOrders.builder()
                    .serviceResponse(objectsDataMapper.ServiceToServiceResponse(service))
                    .currentOrder(orderResponse)
                    .build());
        }
        
        return userOrders;
    }

    /**
     * Retrieves all orders for a specific user
     *
     * @param userId The ID of the user
     * @return List of UserOrders containing order and service information
     * @throws IllegalArgumentException if userId is null
     * @throws ResourceNotFoundException if the user does not exist
     * @throws InternalException if there's an error retrieving the orders
     */
    @Override
    public List<UserOrders> getOrdersByUserId(Long userId) {
        // Validate input
        if (userId == null) {
            log.error("User ID cannot be null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        try {
            // Get user and their assigned services
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User with ID " + userId + " does not exist"));
            
            // Extract service IDs
            Set<Long> serviceIds = user.getWindow().getWindowRoles().stream()
                    .map(windowRole -> windowRole.getService().getId())
                    .collect(Collectors.toSet());
            
            // Get all orders for these services and user
            List<Order> orders = orderRepository.findOrdersByServiceIdsAndUserIdForToday(serviceIds, userId);
            
            return buildUserOrdersResponse(userId, orders);
        } catch (ResourceNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving orders for user {}: {}", userId, e.getMessage());
            e.printStackTrace();
            throw new InternalException("Failed to retrieve orders for user " + userId);
        }
    }

    /**
     * Retrieves orders for a specific user with a specific status
     *
     * @param userId The ID of the user
     * @param orderStatus The status of orders to retrieve
     * @return List of UserOrders containing order and service information
     * @throws IllegalArgumentException if userId or orderStatus is null
     * @throws ResourceNotFoundException if the user does not exist
     * @throws InternalException if there's an error retrieving the orders
     */
    @Override
    public List<UserOrders> getOrdersByUserIdAndStatus(Long userId, OrderStatus orderStatus) {
        // Validate input
        if (userId == null) {
            log.error("User ID cannot be null");
            throw new IllegalArgumentException("User ID cannot be null");
        }
        
        if (orderStatus == null) {
            log.error("Order status cannot be null");
            throw new IllegalArgumentException("Order status cannot be null");
        }
        
        try {
            // Get all orders for this user with the specified status
            List<Order> orders = orderRepository.findOrdersByUserIdAndOrderStatusForToday(userId, orderStatus);
            
            return buildUserOrdersResponse(userId, orders);
        } catch (ResourceNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error retrieving orders for user {} with status {}: {}", userId, orderStatus, e.getMessage());
            throw new InternalException("Failed to retrieve orders for user " + userId);
        }
    }

    /**
     * Recalls a ticket to be displayed again
     *
     * @param orderDAO Data object containing the order ID to recall
     * @throws ResourceNotFoundException if the order does not exist
     */
    @Override
    public void reCallTicket(OrderDAO orderDAO) {
        try {
            // Get the order by ID
            Order order = getOrderById(orderDAO.getOrderId());
            
            // Create an action record for the recall
            createOrderActions(order, OrderStatus.RECALL);
            
            // Send the number to the queue for display
            SendNumberToQueue(
                    order.getCurrentNumber(),
                    order.getUser().getWindow().getWindowNumber(),
                    order.getService().getCode(),
                    order.getUser().getWindow().getIpAddress()
            );
            
            log.info("Recalled ticket for order ID: {}", orderDAO.getOrderId());
        } catch (ResourceNotFoundException e) {
            log.error("Failed to recall ticket: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves the last order called by a specific user
     *
     * @param userId The ID of the user
     * @return OrderResponse containing the last called order, or null if none exists
     */
    @Override
    public OrderResponse getLastCalledOrderByUserId(Long userId) {
        try {
            Optional<Order> order = orderRepository.findTopByUserIdOrderByCallDateDesc(userId);
            
            if (order.isPresent()) {
                return objectsDataMapper.orderToOrderResponse(order.get());
            }
            
            log.info("No called orders found for user ID: {}", userId);
            return null;
        } catch (Exception e) {
            log.error("Error retrieving last called order for user {}: {}", userId, e.getMessage());
            throw new InternalException("Failed to retrieve last called order for user " + userId);
        }
    }

    /**
     * Counts the number of orders with a specific status for a specific service
     *
     * @param serviceId The ID of the service
     * @param orderStatus The status of orders to count
     * @return The count of orders matching the criteria
     */
    @Override
    public Long getCountByServiceIdAndStatus(Long serviceId, OrderStatus orderStatus) {
        try {
            Long count = orderRepository.countByOrderStatusAAndServiceId(orderStatus, serviceId);
            log.debug("Found {} orders with status {} for service ID {}", count, orderStatus, serviceId);
            return count;
        } catch (Exception e) {
            log.error("Error counting orders for service {} with status {}: {}", serviceId, orderStatus, e.getMessage());
            throw new InternalException("Failed to count orders for service " + serviceId);
        }
    }

    @Override
    public List<MainScreenTicket> getLastTickets() {
        ZonedDateTime startOfDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
        List<OrderAction> orderActions= orderActionsRepository.findTop10DistinctByOrderAndStatusAndCreatedAtToday(
                Arrays.asList("RECALL", "BOOKED"), startOfDay, endOfDay);

        List<MainScreenTicket> mainScreenTickets =
                orderActions.stream().map(orderAction -> objectsDataMapper.orderActionToMainScreenTicket(orderAction)).collect(Collectors.toList());
        return mainScreenTickets;

    }

    public void playSound(String fileName) {
        log.info("Start PlaySound {}", fileName);
        Player player;
        // Load the audio file from resources
        ClassPathResource resource = new ClassPathResource("sound/" + fileName);
        //  URL url= new URL("https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav");
        //  InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource.getFile());
        //     AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource.getFile());
        try (InputStream inputStream = resource.getInputStream()) {
            player = new Player(inputStream);
            player.play();

            log.info("End PlaySound {}", fileName);

        } catch (Exception e) {
            log.error("Play Sound Error {}", e.getMessage());
            e.printStackTrace();
        }
        // Obtain a clip to play the audio

        // Close the clip after playback completes

    }
    private long getClipDuration(String fileName) {
        try {
            ClassPathResource resource = new ClassPathResource("sound/" + fileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource.getFile());
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            long duration = clip.getMicrosecondLength() / 1000; // Convert to milliseconds
            clip.close();
            return duration;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
        return 0;
    }
    private User getCurrentUser(){
        User userSec =  (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user= userRepository.findById(userSec.getId()).get();
        return user;
    }

    private void createOrderActions(Order order,OrderStatus orderStatus) {
        OrderAction  orderAction=OrderAction.builder()
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .order(order)
                .orderStatus(orderStatus.toString())
                .build();

       try {
           orderActionsRepository.save(orderAction);
       } catch (Exception e) {
           log.error("createOrderActions error:{}",e.getMessage());
       }
    }
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void updatePendingOrdersToCalled() {
updateOldTickets();
    }
    @Override
    public void   updateOldTickets()
    {
        // Fetch all orders with status PENDING
        ZonedDateTime startOfToday = ZonedDateTime.now().toLocalDate().atStartOfDay(ZonedDateTime.now().getZone());

        // Fetch all orders with status PENDING and createdAt before today
        List<Order> pendingOrdersNotToday = orderRepository.findByTodayAndCreatedAtBefore(true, startOfToday);

        // Update each order's status to CALLED
        pendingOrdersNotToday.forEach(order -> order.setToday(false));

        // Save all updated orders
        orderRepository.saveAll(pendingOrdersNotToday);
    }

    @Override
    public void sendToArduino(String number, String ip) {
        try {
             int PORT = 8888;
            // Log IP and number
            log.info("sendToArduino IP + number - {} - {}", ip, number);

            // Parse the IP address
            InetAddress ipAddress = InetAddress.getByName(ip);

            // Prepare data to send
            byte[] sendBytes = number.getBytes("ASCII");

            // Create the socket and endpoint
            DatagramSocket clientSocket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(sendBytes, sendBytes.length, ipAddress, PORT);

            // Send the data
            clientSocket.send(packet);
            log.info("Send OK");

        } catch (Exception e) {
            log.error("sendToArduino failed - {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates a transfer request for an order
     *
     * @param transferRequest DTO containing transfer request details
     * @return TransferResponse containing the created transfer request details
     * @throws IllegalArgumentException if transferRequest is null or has invalid fields
     * @throws ResourceNotFoundException if any referenced entities don't exist
     */
    @Override
    public TransferResponse createTransferRequest(TransferRequestDTO transferRequest) {
        // Validate input
        if (transferRequest == null) {
            log.error("Transfer request cannot be null");
            throw new IllegalArgumentException("Transfer request cannot be null");
        }
        
        if (transferRequest.getTargetServiceId() == null || 
            transferRequest.getWindowId() == null || 
            transferRequest.getUserId() == null || 
            transferRequest.getServiceId() == null ||
            transferRequest.getOrderId() == null) {
            log.error("Transfer request has null fields");
            throw new IllegalArgumentException("Transfer request has null fields");
        }
        
        // Get all required entities
        ServiceEntity targetService = getServiceEntityById(transferRequest.getTargetServiceId());
        Window window = getWindowById(transferRequest.getWindowId());
        User currentUser = getUserById(transferRequest.getUserId());
        ServiceEntity requestedService = getServiceEntityById(transferRequest.getServiceId());
        
        // Handle hidden service type specially
        if (targetService.getServiceType() == ServiceType.HIDDEN) {
            handleHiddenServiceTransfer(transferRequest, targetService, window, currentUser, requestedService);
        }
        
        // Create and save the transfer request entity
        TransferRequest transferRequestEntity = objectsDataMapper.transferRequestDtoToTransferRequest(
                currentUser, 
                requestedService, 
                window, 
                targetService
        );
        
        transferRequestEntity = transferRequestRepository.save(transferRequestEntity);
        log.info("Created transfer request with ID: {}", transferRequestEntity.getId());
        
        // Map to response and return
        return objectsDataMapper.transferOrderToTransferResponseResponse(transferRequestEntity);
    }
    
    /**
     * Helper method to handle transfer to a hidden service
     */
    private void handleHiddenServiceTransfer(
            TransferRequestDTO transferRequest, 
            ServiceEntity targetService, 
            Window window, 
            User currentUser, 
            ServiceEntity requestedService) {
        
        try {
            // Get the order and transfer it
            Order order = orderRepository.findById(transferRequest.getOrderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Order with ID " + transferRequest.getOrderId() + " not found"));
            
            transferOrder(order, targetService, window, currentUser);
            
            // Update ticket counts for both services
            updateServiceTicketCount(targetService.getId());
            updateServiceTicketCount(requestedService.getId());
            
            log.info("Transferred order {} to hidden service {}", order.getId(), targetService.getId());
        } catch (Exception e) {
            log.error("Failed to transfer order to hidden service: {}", e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to get a service entity by ID
     */
    private ServiceEntity getServiceEntityById(Long serviceId) {
        try {
            ServiceEntity service = serviceService.getServiceById(serviceId);
            if (service == null) {
                throw new ResourceNotFoundException("Service with ID " + serviceId + " not found");
            }
            return service;
        } catch (Exception e) {
            log.error("Failed to get service with ID {}: {}", serviceId, e.getMessage());
            throw new ResourceNotFoundException("Service with ID " + serviceId + " not found");
        }
    }
    
    /**
     * Helper method to get a window by ID
     */
    private Window getWindowById(Long windowId) {
        try {
            Window window = windowService.getWindowByID(windowId);
            if (window == null) {
                throw new ResourceNotFoundException("Window with ID " + windowId + " not found");
            }
            return window;
        } catch (Exception e) {
            log.error("Failed to get window with ID {}: {}", windowId, e.getMessage());
            throw new ResourceNotFoundException("Window with ID " + windowId + " not found");
        }
    }
    
    /**
     * Helper method to get a user by ID
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with ID {} not found", userId);
                    return new ResourceNotFoundException("User with ID " + userId + " not found");
                });
    }

    @Override
    public void  transferOrder (Order order, ServiceEntity targetService,Window window, User user) {
   try {
       order.setService(targetService);
       order.setUser(user);
       order.setWindow(window);
       orderRepository.save(order);
       createOrderActions(order,OrderStatus.TRANSFER);
       log.info("The Order with ID {} Has Been Transferred",order.getId());
   }catch (Exception e){

   }


    }

    @Override
    public boolean approveRequest(Long orderTransferId, Long userId) {
        try{


        TransferRequest transferRequest = transferRequestRepository.findById(orderTransferId).get();
        User user = userRepository.findById(userId).get();

        transferRequest.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
        transferRequest.setResponseUser(user);
        transferRequest.setResponseWindow(user.getWindow());
        transferRequest.setRequestStatus(TransferRequestStatus.APPROVED);
        transferRequestRepository.save(transferRequest);
            Order order = transferRequest.getOrder();

        createOrderActions(order,OrderStatus.APPROVED);
      transferOrder(order,transferRequest.getResponseService(),transferRequest.getResponseWindow(),transferRequest.getResponseUser());

            updateServiceTicketCount(transferRequest.getRequestService().getId());
            updateServiceTicketCount(transferRequest.getResponseService().getId());
        } catch (Exception exception)
        {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void rejectRequest(Long orderTransferId, Long userId) {
        TransferRequest transferRequest = transferRequestRepository.findById(orderTransferId).get();
        User user = userRepository.findById(userId).get();
        transferRequest.setResponseUser(user);
        transferRequest.setRequestStatus(TransferRequestStatus.REJECTED);
        transferRequest.setResponseWindow(user.getWindow());

        transferRequestRepository.save(transferRequest);

        createOrderActions(transferRequest.getOrder(),OrderStatus.REJECTED);
    }
    
    /**
     * Helper method to update the ticket count for a service via notification service
     * 
     * @param serviceId The ID of the service to update the ticket count for
     */
    private void updateServiceTicketCount(Long serviceId) {
        try {
            notificationService.updateServiceTicketsCount(
                    TicketsMessage.builder()
                            .serviceId(serviceId)
                            .ticketCount(orderRepository.countByOrderStatusAAndServiceId(
                                    OrderStatus.PENDING, 
                                    serviceId
                            ))
                            .build()
            );
            log.info("Sent ticket count update to WebSocket for service ID: {}", serviceId);
        } catch (Exception e) {
            log.error("Failed to update ticket count for service {}: {}", serviceId, e.getMessage());
            e.printStackTrace();
        }
    }
}
