package com.dlj4.tech.queue.entity;

import com.dlj4.tech.queue.constants.OrderStatus;
import com.dlj4.tech.queue.constants.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private ZonedDateTime createdAt;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus;
    private String username;
}
