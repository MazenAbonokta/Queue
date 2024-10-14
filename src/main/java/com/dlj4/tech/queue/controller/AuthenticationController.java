package com.dlj4.tech.queue.controller;

import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.service.AuthenticationService;
import com.dlj4.tech.queue.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    UserService userService;
    @GetMapping("/hello")
    public String hello(){
        return  "Hello";
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SigningRequest request){
        JwtAuthenticationResponse response=authenticationService.signIn(request);
        return  new ResponseEntity<JwtAuthenticationResponse>(response, HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> users(){

        return  new ResponseEntity<List<UserResponse>>(userService.getUserResponseList(), HttpStatus.OK);
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> AddUser(@RequestBody UserRequest request){

        return  new ResponseEntity<UserResponse>(authenticationService.signUp(request), HttpStatus.CREATED);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<String> updateUser(@PathVariable("id") Long id,@RequestBody UserRequest request){
        authenticationService.updateUser(request);
        return  new ResponseEntity<String>("User has been update", HttpStatus.OK);
    }
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") Long id){
        authenticationService.deleteUser(id);
        return  new ResponseEntity<String>("User has been deleted",HttpStatus.OK);
    }
}
