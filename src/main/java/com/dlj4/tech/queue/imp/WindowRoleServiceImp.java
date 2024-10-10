package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.WindowRoleDAO;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.entity.WindowRole;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.WindowRoleRepository;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.WindowRoleService;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WindowRoleServiceImp implements WindowRoleService {
    @Autowired
    WindowRoleRepository windowRoleRepository;
    @Autowired
    WindowService windowService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    ServiceService serviceService;
    @Override
    public void AssignRolesToWindow(WindowRoleDAO windowRoleDAO) {
        List<WindowRole> windowRoles =new ArrayList<>();
        Window window=windowService.getWindowByID(windowRoleDAO.getWindowId());
        windowRoles.addAll(windowRoleDAO.getServiceIds()
                .stream()
                .map(s->
                        objectsDataMapper.createWindowEntity(window,serviceService.getServiceById(s)))
                .collect(Collectors.toList()));
        windowRoleRepository.saveAll(windowRoles);
    }
}
