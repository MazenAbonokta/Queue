package com.dlj4.tech.queue.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "WINDOW")
public class Window {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    String ipAddress;
    Long windowNumber;
    @OneToMany(mappedBy = "window", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Order> orders;
    @OneToMany(mappedBy = "window", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WindowRole> windowRoles;

}
