package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.WindowDAO;
import com.dlj4.tech.queue.entity.Window;

public interface WindowService {
    public void createWindow(WindowDAO windowDAO);

    public Window getWindowByID(Long WindowId);
    public void removeWindow(Long windowID);


}
