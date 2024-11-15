package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.ServiceResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.MainScreenTicket;
import com.dlj4.tech.queue.dto.OrderMessageDto;
import com.dlj4.tech.queue.dto.TicketsMessage;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.OrderActionsRepository;
import com.dlj4.tech.queue.repository.OrderRepository;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.TemplatePrintService;
import com.dlj4.tech.queue.service.WindowService;
import jakarta.transaction.Transactional;
import javazoom.jl.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImp implements OrderService {
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ServiceService serviceService;
    @Autowired
    WindowService windowService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TemplatePrintService printService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
@Autowired
OrderActionsRepository orderActionsRepository;
@Autowired
NotificationService notificationService;
    @Value("${queue.name}")
    private String queueName;
   private   Clip clip;
    @Override
    public void createOrder(Long serviceId) {
        ServiceEntity fetchedService = serviceService.getServiceById(serviceId);
        Long currentMaxNumber = orderRepository.findMaxCurrentNumberByServiceId(serviceId);
        Long newCurrenNumber= currentMaxNumber==0?fetchedService.getStart():(currentMaxNumber+1);

        Order order= objectsDataMapper.createOrderEntity(null,fetchedService,newCurrenNumber);
        order=  orderRepository.save(order);
        String Code =order.getService().getCode()+order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try {
            notificationService.updateServiceTicketsCount(
                    TicketsMessage.builder()
                            .serviceId(serviceId)
                            .ticketCount(orderRepository.countByOrderStatusAAndServiceId(OrderStatus.PENDING,serviceId))
                            .build()
            );
            log.info("Send to WebSocket");
        }catch (Exception e){
            e.printStackTrace();
        }
        printService.printTicket("Ticket",Code,order.getService().getName(),"44",order.getCurrentNumber().toString(),"Test" );
    }

    @Override
    public void callNumber(Long Number,String ScreenNumber,String Code) {
            // Load the audio file
        List<String> soundFiles = Arrays.asList("card.mp3","number_" + Number+".mp3",  "window.mp3", "number_"+ ScreenNumber+".mp3");
        try {
            notificationService.sendNewTicketToMainScreen(
                    MainScreenTicket.builder()
                            .ticketNumber(Code+"-"+Number.toString())
                            .counter(ScreenNumber)
                            .build()
            );
            log.info("Send to WebSocket");
        }catch (Exception e){
            e.printStackTrace();
        }
            for (String fileName : soundFiles) {
                playSound(fileName);

                // Wait for the sound to finish playing before starting the next one

            }

    }

    @Override
    public void SendNumberToQueue(Long Number, String ScreenNumber,String Code) {
        log.info("Send Ticket Number To The {} Queue For Calling",Number);

        var orderMessageDTo=new OrderMessageDto(Number,ScreenNumber,Code);
        rabbitTemplate.convertAndSend(queueName, orderMessageDTo);
        log.info("Is the Communication request successfully triggered ?");
    }

    @Override
    public UserOrders fetchNextOrder(OrderDAO orderDAO) {

        try {
            updateCurrentOrder(orderDAO);
        }
        catch (Exception e) {
            log.info(e.getMessage());
        }
        User user =getCurrentUser();
        Order nextOrder= orderRepository.
                findFirstByOrderStatusAndServiceId(OrderStatus.PENDING,orderDAO.getServiceId());
        Window window = windowService.getWindowByID(user.getWindow().getId());
        nextOrder.setOrderStatus(OrderStatus.BOOKED);
        nextOrder.setCallDate(ZonedDateTime.now(ZoneId.of("UTC")));
        nextOrder.setWindow(window);
        nextOrder.setUser(user);
        orderRepository.save(nextOrder);
        createOrderActions(nextOrder,OrderStatus.BOOKED);


       SendNumberToQueue(nextOrder.getCurrentNumber(),user.getWindow().getWindowNumber(),nextOrder.getService().getCode());

        OrderResponse orderResponse= objectsDataMapper.orderToOrderResponse(nextOrder);

        ServiceEntity service = serviceService.getServiceById(orderDAO.getServiceId());
        ServiceResponse serviceResponse=objectsDataMapper.ServiceToServiceResponse(service);

        try {
            notificationService.updateServiceTicketsCount(
                    TicketsMessage.builder()
                            .serviceId(orderDAO.getServiceId())
                            .ticketCount(orderRepository.countByOrderStatusAAndServiceId(OrderStatus.PENDING,orderDAO.getServiceId()))
                            .build()
            );
            log.info("Send to WebSocket");
        }catch (Exception e){
            e.printStackTrace();
        }
        return  UserOrders.builder()
                .currentOrder(orderResponse)
                .serviceResponse(serviceResponse)
                .build();

    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus orderStatus) {


        order.setOrderStatus(orderStatus);
        order.setUser(getCurrentUser());

        orderRepository.save(order);
        createOrderActions(order,orderStatus);
    }

    private void updateCurrentOrder(OrderDAO orderDAO) {
        if(orderDAO.getOrderId()!=0)
        {
            Order CurrenOrder= getOrderById(orderDAO.getOrderId());
            if(CurrenOrder!=null){
                updateOrderStatus(CurrenOrder,orderDAO.getOrderStatus());
            }
        }


    }
    @Override
    public Order getOrderById(Long OrderId) {
        Order order= orderRepository.findById(OrderId)
                .orElseThrow(()->new ResourceNotFoundException("service ["+OrderId +"is not Exist"));
        return  order;
    }

    @Override
    public List<UserOrders> getOrdersByUserId(Long userId) {
        User user= userRepository.findById(userId).get();
        List<ServiceEntity> serviceEntities=  user.getWindow().getWindowRoles().stream().map(x->x.getService()
        ).collect(Collectors.toList());
        Set<Long> ServiceIds= serviceEntities.stream().map(x->x.getId()
        ).collect(Collectors.toSet());
        List<Order> orders = orderRepository.findByServiceIsInAndUserId(ServiceIds,userId);
        List<UserOrders> userOrders = new ArrayList<>();
        List<OrderResponse> orderResponses = orders.stream().map(orderObj-> objectsDataMapper.orderToOrderResponse(orderObj)).collect(Collectors.toList());
        Map<Long, OrderResponse> orderResponseMap = orderResponses.stream()
                .collect(Collectors.toMap(OrderResponse::getServiceId, orderResponse -> orderResponse));

        for (ServiceEntity service : serviceEntities)
        {
            OrderResponse orderResponse= orderResponseMap.get(service.getId());
            userOrders.add(UserOrders.builder()
                            .serviceResponse(objectsDataMapper.ServiceToServiceResponse(service))
                            .currentOrder(orderResponse)

                    .build());
        }
        return userOrders;
    }

    @Override
    public void reCallTicket(OrderDAO orderDAO) {
        Order order= getOrderById(orderDAO.getOrderId());
        createOrderActions(order,OrderStatus.RECALL);
        SendNumberToQueue(order.getCurrentNumber(),order.getUser().getWindow().getWindowNumber(),order.getService().getCode());
    }

    @Override
    public OrderResponse getLastCalledOrderByUserId(Long userId) {
        Optional<Order> order=orderRepository.findTopByUserIdOrderByCallDateDesc(userId);
        if(order.isPresent()){

            return objectsDataMapper.orderToOrderResponse(order.get());
        }

        return null;
    }

    @Override
    public Long getCountByServiceIdAndStatus(Long serviceId, OrderStatus orderStatus) {
        return  orderRepository.countByOrderStatusAAndServiceId(orderStatus,serviceId);

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
}
