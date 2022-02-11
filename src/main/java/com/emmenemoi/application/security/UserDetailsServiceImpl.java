package com.emmenemoi.application.security;

import com.emmenemoi.application.data.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService, UserService {
    static Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    Set<User> users;

    public UserDetailsServiceImpl( @Value("${security.users}") String usersFilePath) {
        super();
        mapper.findAndRegisterModules();
        try {
            File source = StringUtils.startsWithIgnoreCase(usersFilePath,"classpath:") ?
                    ResourceUtils.getFile(usersFilePath)
                    : new File(usersFilePath);
            logger.info("Loading users from {}", source.getPath());
            users = mapper.readValue(source, new TypeReference<Set<User>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<User> loadUserByName(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = loadUserByName(username).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("No user present with username: " + username);
        } else {
            return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getHashedPassword(),
                    getAuthorities(user));
        }
    }

    private static List<GrantedAuthority> getAuthorities(User user) {
        return user.getRoles().stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRoleName()))
                .collect(Collectors.toList());

    }

}
