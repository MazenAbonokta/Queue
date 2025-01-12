package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.constants.UserStatus;
import com.dlj4.tech.queue.entity.User;

import javax.lang.model.element.Name;

public interface UserActionService {

    public void AddNewAction(String username, UserStatus userStatus);
}
