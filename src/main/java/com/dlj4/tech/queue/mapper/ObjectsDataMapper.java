package com.dlj4.tech.queue.mapper;

import com.dlj4.tech.queue.dto.ServiceDTO;
import com.dlj4.tech.queue.dto.WindowDTO;
import com.dlj4.tech.queue.entity.Category;
import com.dlj4.tech.queue.entity.Service;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.entity.WindowRole;
import org.springframework.stereotype.Component;

@Component
public class ObjectsDataMapper {

    public Service serviceDTOToServiceEntity(ServiceDTO serviceDTO, Category category){
        return Service.builder()
                .start(serviceDTO.getStart())
                .end(serviceDTO.getEnd())
                .category(category)
                .code(serviceDTO.getCode())

                .build();

    }
    public Window windowDTOToWindowEntity(WindowDTO windowDTO){
        return Window.builder()
                .windowNumber(windowDTO.getWindowNumber())
                .ipAddress(windowDTO.getIpAddress())
                .build();
    }
    public WindowRole createWindowEntity(Window window,Service service){
        return WindowRole.builder()
                .window(window)
                .service(service)
                .build();
    }

}
