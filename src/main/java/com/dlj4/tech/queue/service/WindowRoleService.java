package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.WindowRoleDAO;
import com.dlj4.tech.queue.entity.WindowRole;

import java.util.List;

public interface WindowRoleService {
    public  List<WindowRole> AssignRolesToWindow(WindowRoleDAO windowRoleDAO);
    public  List<WindowRole> getUpdateRole(WindowRoleDAO windowRoleDAO);
    List<WindowRole> mergeServiceWindows(List<WindowRole> list1, List<WindowRole> list2);
    public void createNewRoles(List<WindowRole> windowRoles);
}
