package com.emmenemoi.application.data.entity;

import com.emmenemoi.application.data.Role;

import java.util.Set;

public class User  {

    private String username;
    private String hashedPassword;
    private Set<Role> roles;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getHashedPassword() {
        return hashedPassword;
    }
    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }
    public Set<Role> getRoles() {
        return roles;
    }
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

}
