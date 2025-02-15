package com.dlj4.tech.queue.imp;
import com.dlj4.tech.queue.constants.UserStatus;
import com.dlj4.tech.queue.dao.request.UserRequest;
import com.dlj4.tech.queue.dao.response.UserResponse;
import com.dlj4.tech.queue.entity.Token;
import com.dlj4.tech.queue.entity.User;
import com.dlj4.tech.queue.entity.UserActions;
import com.dlj4.tech.queue.entity.Window;
import com.dlj4.tech.queue.exception.RefreshTokenNotFoundException;
import com.dlj4.tech.queue.exception.ResourceAlreadyExistException;
import com.dlj4.tech.queue.exception.ResourceNotFoundException;
import com.dlj4.tech.queue.mapper.ObjectsDataMapper;
import com.dlj4.tech.queue.repository.TokenRepository;
import com.dlj4.tech.queue.repository.UserRepository;
import com.dlj4.tech.queue.dao.request.RefreshRequest;
import com.dlj4.tech.queue.dao.request.SigningRequest;
import com.dlj4.tech.queue.dao.response.JwtAuthenticationResponse;
import com.dlj4.tech.queue.service.AuthenticationService;
import com.dlj4.tech.queue.service.JwtService;
import com.dlj4.tech.queue.service.UserActionService;
import com.dlj4.tech.queue.service.WindowService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service

public class AuthenticationServiceImpl implements AuthenticationService {
    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  TokenRepository tokenRepository;
    @Autowired
    private  JwtService jwtService;
    @Autowired
    ObjectsDataMapper objectsDataMapper;
    @Autowired
    private  AuthenticationManager authenticationManager;
    @Autowired
    WindowService  windowService;
    @Value("${token.access.token.expiration}")
    private long accessTokenExpiration;
    @Autowired
    private UserActionService userActionService;
    @Transactional
    @Override
    public JwtAuthenticationResponse signIn(SigningRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
        List<Token> activeTokens = tokenRepository.findAllByUserAndIsActive(user, true).orElse(Collections.emptyList());
        if (!activeTokens.isEmpty()) {
            for (Token activeToken : activeTokens) {
                activeToken.setIsActive(false);
            }
            tokenRepository.saveAll(activeTokens);
        }
        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        var expiresAt = new Date(System.currentTimeMillis() + accessTokenExpiration);
        tokenRepository.save(new Token(jwt, refreshToken, expiresAt, user, true));
        userActionService.AddNewAction(user.getUsername(), UserStatus.LOGIN);
        return JwtAuthenticationResponse.builder().token(jwt).refreshToken(refreshToken).expiresAt(expiresAt).status("success")
                .WindowId(user.getWindow()==null?0:user.getWindow().getId())
                .WindowNumber(user.getWindow()==null?"0":user.getWindow().getWindowNumber())
                .Role(user.getRole().toString())
                .build();
    }
    @Override
    public JwtAuthenticationResponse refreshToken(RefreshRequest request) {
        Token token = tokenRepository.findByRefreshToken(request.getRefreshToken()).orElseThrow(() -> new RefreshTokenNotFoundException(request.getRefreshToken()));
        if (token.getExpiresAt().after(new Date()) && token.getIsActive()) {
            var user = token.getUser();
            var newAccessToken = jwtService.generateToken(user);
            var newRefreshToken = jwtService.generateRefreshToken(user);
            var expiresAt = new Date(System.currentTimeMillis() + accessTokenExpiration);
            token.setIsActive(false);
            tokenRepository.save(token);
            tokenRepository.save(new Token(newAccessToken, newRefreshToken, expiresAt, user, true));
            return new JwtAuthenticationResponse(newAccessToken, newRefreshToken, expiresAt,"success",user.getWindow().getId(),user.getWindow().getWindowNumber(),user.getRole().toString());

        } else {
            tokenRepository.delete(token);
            return null;
        }
    }
    @Override
    public UserResponse signUp(UserRequest userRequest) {

        Window window= null;
        window =userRequest.getWindowId()==""?null:windowService.getWindowByID(Long.parseLong(userRequest.getWindowId()));

        User user = objectsDataMapper.userDTOToUser(userRequest,window);
        Optional<User> checkedUser=userRepository.findByUsername(userRequest.getUsername());
        if(checkedUser.isPresent())
        {
            throw new ResourceAlreadyExistException("User ["+ userRequest.getUsername()+"]");
        }
        user=  userRepository.save(user);
       UserResponse response=objectsDataMapper.userToUserResponse(user);
       return  response;
    }
    @Override
    public void updateUser(UserRequest userRequest) {
        Window window= null;
        window =userRequest.getWindowId()==""?null:windowService.getWindowByID(Long.parseLong(userRequest.getWindowId()));

      User user=userRepository.findByUsername(userRequest.getUsername())
              .orElseThrow(()-> new ResourceNotFoundException("User not Exist"));
user.setWindow(window);
      user=objectsDataMapper.copyUserRequestToUser(user,userRequest);
      userRepository.save(user);
    }
    @Override
    public void deleteUser(Long id) {
       User user= userRepository.findById(id).orElseThrow(
               ()->new ResourceNotFoundException("UserNotFound"));
       user.setDeleted(true);
       userRepository.save(user);
    }

    @Override
    public User findUserByUsername(String username) {

      Optional<User> user=  userRepository.findByUsername(username);
      if(user.isPresent())
      {
          return user.get();
      }
        return null;
    }
}