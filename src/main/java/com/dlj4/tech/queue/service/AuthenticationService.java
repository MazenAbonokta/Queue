package com.dlj4.tech.queue.service;

import com.dlj4.tech.queue.dao.request.RefreshRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.dao.response.UserResponse;

public interface AuthenticationService {
   JwtAuthenticationResponse   signIn(SigningRequest request);
    JwtAuthenticationResponse refreshToken(RefreshRequest request);
    public UserResponse signUp(UserRequest userRequest);
    public void updateUser(UserRequest userRequest);
    public void deleteUser(Long id);





}
