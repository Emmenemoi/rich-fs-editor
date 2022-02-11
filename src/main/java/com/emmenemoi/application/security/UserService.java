package com.emmenemoi.application.security;

import com.emmenemoi.application.data.entity.User;

import java.util.Optional;

public interface UserService {
    Optional<User> loadUserByName(String username);
}
