package com.dlj4.tech.queue.entity;

import com.dlj4.tech.queue.constants.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    Long currentNumber;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime callDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    String windowNumber;
    private  String serviceName;
    private  String categoryName;
    private String username;

}
