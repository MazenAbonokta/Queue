package com.dlj4.tech.queue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "window_entity")
@Where(clause = "deleted=false")
public class Window {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String ipAddress;
    String windowNumber;
    @OneToMany(mappedBy = "window", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orders;
    @OneToMany(mappedBy = "window", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WindowRole> windowRoles;
    @OneToMany(mappedBy = "window", cascade = CascadeType.MERGE, orphanRemoval = true)
    List<User> users;

    @OneToMany(mappedBy = "window", cascade = CascadeType.MERGE, orphanRemoval = true)
    List<TransferRequest> requestedOrders;

    @OneToMany(mappedBy = "window", cascade = CascadeType.MERGE, orphanRemoval = true)
    List<TransferRequest> approvedOrders;
    private boolean deleted = Boolean.FALSE;


}
