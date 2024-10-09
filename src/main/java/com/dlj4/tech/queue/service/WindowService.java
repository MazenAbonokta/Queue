package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dto.WindowDTO;
import com.dlj4.tech.queue.dto.WindowRoleDTO;
import com.dlj4.tech.queue.entity.Window;

public interface WindowService {
    public void createWindow(WindowDTO windowDTO);

    public Window getWindowByID(Long WindowId);
    public void removeWindow(Long windowID);


}
