package com.dlj4.tech.queue.dao.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ConfigRequest {
    private String name;
    private String id;
    private String editor;
    private String fileExt;
    private String configType;
    private String img;
}
