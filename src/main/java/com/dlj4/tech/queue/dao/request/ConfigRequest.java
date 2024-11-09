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
    private Long id;
    private String mainScreenName;
    private String mainScreenFileExtension;
    private String mainScreenOriginalName;
    private String logoName;
    private String logoFileExtension;
    private String logoOriginalName;
    private  String mainScreenMessage;
    private  String ticketScreenMessage;
    private String logoImg;
    private  String mainScreenImg;
}
