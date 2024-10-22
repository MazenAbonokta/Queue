package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.WindowRoleDAO;
import com.dlj4.tech.queue.entity.WindowRole;

import java.util.List;

public interface WindowRoleService {
    public  List<WindowRole> AssignRolesToWindow(WindowRoleDAO windowRoleDAO);

    public void createNewRoles(List<WindowRole> windowRoles);
}
