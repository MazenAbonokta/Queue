package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.OrderDAO;
import com.dlj4.tech.queue.dao.response.OrderResponse;
import com.dlj4.tech.queue.dto.OrderMessageDto;
import com.dlj4.tech.queue.entity.Order;
import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.enums.OrderStatus;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.OrderRepository;
import com.dlj4.tech.queue.service.OrderService;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.TemplatePrintService;
import com.dlj4.tech.queue.service.WindowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
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
    TemplatePrintService printService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
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
    public void callNumber(Long Number,Long ScreenNumber) {

            // Load the audio file


            List<String> soundFiles = Arrays.asList("card.wav","number_" + Number+".wav",  "window.wav", "number_"+ ScreenNumber+".wav");

            for (String fileName : soundFiles) {
                playSound(fileName);

                // Wait for the sound to finish playing before starting the next one

            }

    }

    @Override
    public void SendNumberToQueue(Long Number, Long ScreenNumber) {
        log.info("Send Ticket Number To The {} Queue For Calling",Number);

        var orderMessageDTo=new OrderMessageDto(Number,ScreenNumber);
        rabbitTemplate.convertAndSend(queueName, orderMessageDTo);
        log.info("Is the Communication request successfully triggered ? : {}");
    }

    @Override
    public Order fetchNextOrder(OrderDAO orderDAO) {

        Order CurrenOrder=getOrderById(orderDAO.getOrderId());
        updateOrderStatus(CurrenOrder,orderDAO.getOrderStatus());
        Order nextOrder= orderRepository.
                findOrderByOrderStatusAndService_IdOrderByIdDesc(orderDAO.getOrderStatus(),orderDAO.getServiceId());
        Window window = windowService.getWindowByID(orderDAO.getWindowId());
        nextOrder.setOrderStatus(OrderStatus.BOOKED);
        nextOrder.setCallDate(ZonedDateTime.now(ZoneId.of("UTC")));
        nextOrder.setWindow(window);
        orderRepository.save(nextOrder);


       SendNumberToQueue(nextOrder.getCurrentNumber(),orderDAO.getWindowId());


        return  nextOrder;

    }

    @Override
    public void updateOrderStatus(Order order, OrderStatus orderStatus) {


        order.setOrderStatus(orderStatus);
        orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long OrderId) {
        Order order= orderRepository.findById(OrderId)
                .orElseThrow(()->new ResourceNotFoundException("service ["+OrderId +"is not Exist"));
        return  order;
    }

    @Override
    public List<OrderResponse> getOrdersByUser(User user) {

        Set<Long> ServiceIds= user.getWindow().getWindowRoles().stream().map(x->x.getService().getId()
        ).collect(Collectors.toSet());
        List<Order> orders = orderRepository.findByServiceIsIn(ServiceIds);
        return List.of();
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
}
