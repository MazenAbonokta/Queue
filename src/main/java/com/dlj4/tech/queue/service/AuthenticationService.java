package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.RefreshRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.request.UserDAO;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.entity.User;

public interface AuthenticationService {
   JwtAuthenticationResponse   signIn(SigningRequest request);
    JwtAuthenticationResponse refreshToken(RefreshRequest request);
    public  void  signUp(UserDAO userDAO);





}
