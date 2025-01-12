package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.constants.UserStatus;
import com.dlj4.tech.queue.entity.UserActions;
import com.dlj4.tech.queue.repository.UserActionRepository;
import com.dlj4.tech.queue.service.UserActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class UserActionServiceImp implements UserActionService {
    @Autowired
    UserActionRepository userActionRepository;


    @Override
    public void AddNewAction(String username, UserStatus userStatus) {
        UserActions userActions= UserActions.builder()
                .username(username)
                .userStatus(userStatus)
                .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
                .build();
    }
}
