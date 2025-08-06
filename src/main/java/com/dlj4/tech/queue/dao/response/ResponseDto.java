package com.dlj4.tech.queue.dao.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class ResponseDto {


        private  String apiPath;


        private HttpStatus code;


        private  String message;


        private LocalDateTime time;

}