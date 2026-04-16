package com.example.bankcards.service.user;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;

public interface UserService {

    UserResponse create(UserRequest request);



}
