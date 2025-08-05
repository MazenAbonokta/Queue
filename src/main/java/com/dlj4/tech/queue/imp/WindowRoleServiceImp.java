package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.WindowRoleDAO;

import com.dlj4.tech.queue.entity.WindowRole;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.WindowRoleRepository;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.WindowRoleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WindowRoleServiceImp implements WindowRoleService {
    @Autowired
    WindowRoleRepository windowRoleRepository;

    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    ServiceService serviceService;
    @Override
    public List<WindowRole> AssignRolesToWindow(WindowRoleDAO windowRoleDAO) {
        List<WindowRole> windowRoles =new ArrayList<>();

        windowRoles.addAll(windowRoleDAO.getServiceIds()
                .stream()
                .map(s->
                        objectsDataMapper.createWindowRoleEntity(windowRoleDAO.getWindow(),serviceService.getServiceById(s)))
                .collect(Collectors.toList()));
       return windowRoleRepository.saveAll(windowRoles);
    }

    @Override
    public List<WindowRole> getUpdateRole(WindowRoleDAO windowRoleDAO) {
        List<WindowRole> windowRoles =new ArrayList<>();

        windowRoles.addAll(windowRoleDAO.getServiceIds()
                .stream()
                .map(s->
                        objectsDataMapper.createWindowRoleEntity(windowRoleDAO.getWindow(),serviceService.getServiceById(s)))
                .collect(Collectors.toList()));
        return  windowRoles;
    }
    @Override
    public  List<WindowRole> mergeServiceWindows(List<WindowRole> list1, List<WindowRole> list2) {
        // Create a map for fast lookup for list1
        Map<Long, WindowRole> list1Map = list1.stream()
                .collect(Collectors.toMap(x->x.getService().getId(), Function.identity()));

        Map<Long, WindowRole> list2Map = list2.stream()
                .collect(Collectors.toMap(x->x.getService().getId(), Function.identity()));

        List<WindowRole> result = new ArrayList<>();

        // Add elements from list1 that have matching serviceId in list2
        // Objects to keep (exist in both)
        List<WindowRole> toKeep = list2.stream()
                .filter(sw -> list1Map.containsKey(sw.getService().getId()))
                .map(sw -> list1Map.get(sw.getService().getId())) // Get the object from list1
                .collect(Collectors.toList());

        // Objects to add (exist in list2 but not in list1)
        List<WindowRole> toAdd = list2.stream()
                .filter(sw -> !list1Map.containsKey(sw.getService().getId()))
                .collect(Collectors.toList());
        List<WindowRole> addedRoles= windowRoleRepository.saveAll(toAdd);
        // Objects to remove (exist in list1 but not in list2)
        List<WindowRole> toRemove = list1.stream()
                .filter(sw -> !list2Map.containsKey(sw.getService().getId()))
                .collect(Collectors.toList());
        windowRoleRepository.deleteAll(toRemove);
        result.addAll(addedRoles);
        result.addAll(toKeep);
        return result;
    }
    @Override
    public void createNewRoles(List<WindowRole> windowRoles) {

    }
}
