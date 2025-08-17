package com.dlj4.tech.queue.entity;

import com.dlj4.tech.queue.constants.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ORDERS")
@Builder
@Where(clause = "today=true AND deleted=false")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    Long currentNumber;
    private ZonedDateTime updatedAt;
    private ZonedDateTime createdAt;
    private ZonedDateTime callDate;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Builder.Default
    private boolean today = true;

    private String code;
    @Builder.Default
    private boolean deleted = Boolean.FALSE;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "window_id")
    Window window;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "service_id")

    ServiceEntity service;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderAction> orderActions;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    List<OrderAction> orderConvertRequests;
}
