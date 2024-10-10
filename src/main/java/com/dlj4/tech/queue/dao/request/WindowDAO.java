package com.dlj4.tech.queue.dao.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WindowDAO {
    String ipAddress;
    Long windowNumber;
}
