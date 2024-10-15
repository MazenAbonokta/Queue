package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.WindowRequest;
import com.dlj4.tech.queue.dao.response.WindowResponse;
import com.dlj4.tech.queue.entity.Window;

import java.util.List;

public interface WindowService {
    public WindowResponse createWindow(WindowRequest windowRequest);

    public Window getWindowByID(Long WindowId);
    public void deleteWindow(Long windowID);

    public List<WindowResponse> getWindowsList();
    public void updateWindow(Long id,WindowRequest windowRequest);


}
