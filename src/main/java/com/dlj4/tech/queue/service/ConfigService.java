package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.ConfigRequest;
import com.dlj4.tech.queue.dao.response.ConfigResponse;

public interface ConfigService {
    public ConfigResponse createConfig(ConfigRequest configRequest);
    public ConfigResponse updateConfig(ConfigRequest configRequest);
    public ConfigResponse getConfigByType(String configType);
}
