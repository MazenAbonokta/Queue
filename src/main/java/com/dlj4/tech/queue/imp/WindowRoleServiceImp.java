package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dto.WindowRoleDTO;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.entity.WindowRole;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.WindowRoleRepository;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.WindowRoleService;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    public void AssignRolesToWindow(WindowRoleDTO windowRoleDTO) {
        List<WindowRole> windowRoles =new ArrayList<>();
        Window window=windowService.getWindowByID(windowRoleDTO.getWindowId());
        windowRoles.addAll(windowRoleDTO.getServiceIds()
                .stream()
                .map(s->
                        objectsDataMapper.createWindowEntity(window,serviceService.getServiceById(s)))
                .collect(Collectors.toList()));
        windowRoleRepository.saveAll(windowRoles);
    }
}
