package com.dlj4.tech.queue.entity;

import com.dlj4.tech.queue.constants.ServiceStatus;
import com.dlj4.tech.queue.constants.ServiceType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "SERVICE")
@Where(clause = "deleted=false")
public class ServiceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String  code;
    private int start;
    private  int end;
    private LocalTime endTime;
    private  String name;

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;
    @Enumerated(EnumType.STRING)
    private ServiceType Type;
    private boolean deleted = Boolean.FALSE;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orders;
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WindowRole> windowRoles;


    @OneToMany(mappedBy = "requestService", cascade = CascadeType.MERGE, orphanRemoval = true)
    List<TransferRequest> requestedOrders;

    @OneToMany(mappedBy = "responseService", cascade = CascadeType.MERGE, orphanRemoval = true)
    List<TransferRequest> approvedOrders;
}
