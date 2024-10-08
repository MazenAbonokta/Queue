package com.dlj4.tech.queue.imp;

import com.dlj4.tech.queue.dao.request.WindowDAO;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.WindowRepository;
import com.dlj4.tech.queue.service.ServiceService;
import com.dlj4.tech.queue.service.WindowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WindowServiceImp implements WindowService {
    @Autowired
    WindowRepository windowRepository;
    @Autowired
    ServiceService serviceService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Override
    public void createWindow(WindowDAO windowDAO) {

        validateIpAddress(windowDAO.getIpAddress());
        Window window = objectsDataMapper.windowDTOToWindowEntity(windowDAO);
        windowRepository.save(window);
    }

    @Override
    public Window getWindowByID(Long WindowId) {
        Optional<Window> window=windowRepository.findById(WindowId);
        if (window.isPresent())
        {
            throw  new ResourceNotFoundException("Ip Address ["+WindowId +"is not Exist");
        }
        return  window.get();
    }

    @Override
    public void removeWindow(Long windowID) {
        Window window =getWindowByID(windowID);
        windowRepository.delete(window);

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
