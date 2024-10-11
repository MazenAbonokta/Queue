package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.entity.Token;
import com.dlj4.tech.queue.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Authentication")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @GetMapping("/hello")
    public String hello(){
        return  "Hello";
    }

    @PostMapping("/signIn")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SigningRequest request){
        JwtAuthenticationResponse response=authenticationService.signIn(request);
        return  new ResponseEntity<JwtAuthenticationResponse>(response, HttpStatus.OK);
    }
}
