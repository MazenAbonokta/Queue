package com.dlj4.tech.queue.dao.response;

import com.dlj4.tech.queue.constants.TransferRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class TransferResponse {
    Long requestId;
    String order;
    String userRequester;
    String requestDate;
    String requestedService;
    String requestedWindow;
    String targetService;
    String userResponse;
    String targetWindow;
    String responseDate;
    TransferRequestStatus status;

}
