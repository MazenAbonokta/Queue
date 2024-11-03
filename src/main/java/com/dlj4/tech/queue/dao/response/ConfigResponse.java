package com.dlj4.tech.queue.dao.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfigResponse {
    private String name;
    private String id;
    private String content;
    private String fullPath;
    private String fileExt;
    private String configType;
    private String img;
}
