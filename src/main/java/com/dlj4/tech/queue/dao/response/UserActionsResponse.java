package com.dlj4.tech.queue.dao.response;

import com.dlj4.tech.queue.constants.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserActionsResponse {
    private Long id;
    private ZonedDateTime createdAt;
    private UserStatus userStatus;
    private String username;
}
