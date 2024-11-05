package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.request.WindowRoleDAO;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.entity.ServiceEntity;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.entity.WindowRole;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.WindowRepository;
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
public class WindowServiceImp implements WindowService {
    @Autowired
    WindowRepository windowRepository;
    @Autowired
    ServiceService serviceService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    WindowRoleService windowRoleService;
    @Override
    public WindowResponse createWindow(WindowRequest windowRequest) {

        validateIpAddress(windowRequest.getIpAddress());
        Window window = objectsDataMapper.windowDTOToWindowEntity(windowRequest);

        window=  windowRepository.save(window);
        WindowRoleDAO dao=objectsDataMapper.windowToWindowRoleDAO(window,windowRequest.getServices());
        List<WindowRole> windowRoles= windowRoleService.AssignRolesToWindow(dao);
        window.setWindowRoles(windowRoles);
        return objectsDataMapper.windowToWindowResponse(window);
    }

    @Override
    public Window getWindowByID(Long WindowId) {
        Optional<Window> window=windowRepository.findById(WindowId);
        if (window.isEmpty())
        {
            throw  new ResourceNotFoundException("Ip Address ["+WindowId +"is not Exist");
        }
        return  window.get();
    }

    @Override
    public void deleteWindow(Long windowID) {
        Window window =getWindowByID(windowID);
        windowRepository.delete(window);

    }

    @Override
    public List<WindowResponse> getWindowsList() {
        List<Window> windows= windowRepository.findAll();
        List<WindowResponse> responses=windows.stream().map(
                window -> objectsDataMapper.windowToWindowResponse(window)
        ).collect(Collectors.toList());
        return  responses;
    }

    @Override
    public WindowResponse updateWindow(Long id, WindowRequest windowRequest) {
        Window window= getWindowByID(id);
        window= objectsDataMapper.copyWindowRequestToWindow(windowRequest,window);
        windowRepository.save(window);
        return objectsDataMapper.windowToWindowResponse(window);

    }

    private void validateIpAddress(String IpAddress)
    {
        Optional<Window> window=windowRepository.findByIpAddress(IpAddress);
        if (window.isPresent())
        {
            throw  new ResourceAlreadyExistException("Ip Address ["+IpAddress +"is Already Exist");
        }
    }

}
