package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dao.response.UserOrders;
import com.dlj4.tech.queue.dto.OrderMessageDto;
import com.dlj4.tech.queue.entity.*;
import com.dlj4.tech.queue.enums.OrderStatus;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.OrderActionsRepository;
import com.dlj4.tech.queue.repository.OrderRepository;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.TemplatePrintService;
import com.dlj4.tech.queue.service.WindowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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
        printService.printTicket("Ticket",Code,order.getService().getName(),"44",order.getCurrentNumber().toString(),"Test" );
    }

    @Override
    public void callNumber(Long Number,String ScreenNumber) {
            // Load the audio file
        List<String> soundFiles = Arrays.asList("card_2.wav","number_" + Number+".wav",  "window.wav", "number_"+ ScreenNumber+".wav");

            for (String fileName : soundFiles) {
                playSound(fileName);

                // Wait for the sound to finish playing before starting the next one

            }

    }

    @Override
    public void SendNumberToQueue(Long Number, String ScreenNumber) {
        log.info("Send Ticket Number To The {} Queue For Calling",Number);

        var orderMessageDTo=new OrderMessageDto(Number,ScreenNumber);
        rabbitTemplate.convertAndSend(queueName, orderMessageDTo);
        log.info("Is the Communication request successfully triggered ?");
    }

    @Override
    public OrderResponse fetchNextOrder(OrderDAO orderDAO) {

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


       SendNumberToQueue(nextOrder.getCurrentNumber(),user.getWindow().getWindowNumber());

        OrderResponse orderResponse= objectsDataMapper.orderToOrderResponse(nextOrder);

        return  orderResponse;

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
        SendNumberToQueue(order.getCurrentNumber(),order.getUser().getWindow().getWindowNumber());
    }

    @Override
    public OrderResponse getLastCalledOrderByUserId(Long userId) {
        Optional<Order> order=orderRepository.findTopByUserIdOrderByCallDateDesc(userId);
        if(order.isPresent()){

            return objectsDataMapper.orderToOrderResponse(order.get());
        }

        return null;
    }

    public void playSound(String fileName) {
        try {
            // Load the audio file from resources
            ClassPathResource resource = new ClassPathResource("sound/" + fileName);
          //  URL url= new URL("https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav");
          //  InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource.getFile());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource.getFile());

            // Obtain a clip to play the audio
            clip = AudioSystem.getClip();

            clip.open(audioStream);
            // Start playing the sound
            System.out.println("Start " + fileName);
            long duration = clip.getMicrosecondLength() / 1000; // Convert to milliseconds
            System.out.println("duration{"+duration+"} "+ fileName);


            clip.start();

            Thread.sleep(duration);

            System.out.println("End duration{"+duration+"} "+fileName);
            // clip.close();
            System.out.println("Close  duration{"+duration+"} "+fileName);
            // Close the clip after playback completes

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
}
