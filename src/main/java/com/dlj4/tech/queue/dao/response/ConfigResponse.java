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
    private String id;
    private String mainScreenName;
    private String mainScreenFileExtension;
    private String mainScreenOriginalName;
    private String logoName;
    byte[] logImg;
    byte[] mainScreenImg;
    private String logoFileExtension;
    private String logoOriginalName;
    private  String mainScreenMessage;
    private  String ticketScreenMessage;
}
