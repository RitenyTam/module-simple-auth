package com.riteny.simpleauth.datasource.impl;

import com.riteny.simpleauth.datasource.UserDatasource;
import com.riteny.simpleauth.entity.SimpleAuthUser;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUserDataSource implements UserDatasource {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUserDataSource.class);

    private static final Map<String, SimpleAuthUser> userMap = new HashMap<>();

    public PropertiesUserDataSource() {

        Properties props = new Properties();

        try (InputStream fileInputStream = this.getClass().getClassLoader().getResourceAsStream("file:user.properties");
             InputStream classpathInputStream = this.getClass().getClassLoader().getResourceAsStream("user.properties")) {

            if (fileInputStream == null && classpathInputStream == null) {
                throw new RuntimeException("user.properties not found");
            } else if (fileInputStream == null) {
                props.load(classpathInputStream);
            } else {
                props.load(fileInputStream);
            }

            props.keySet().forEach(key -> {
                String username = key.toString().replace("simple.auth.user.", "");
                String password = props.get(key).toString();

                if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
                    SimpleAuthUser simpleAuthUser = new SimpleAuthUser();
                    simpleAuthUser.setUsername(username);
                    simpleAuthUser.setPassword(password);
                    userMap.put(username, simpleAuthUser);
                } else {
                    logger.warn("Username or password is empty, username : {}  ,   password : {} ", username, password);
                }
            });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SimpleAuthUser searchUserByUsernameAndPassword(String username, String password) {

        SimpleAuthUser user = userMap.get(username);

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }

    @Override
    public SimpleAuthUser searchUserByUsername(String username) {
        return userMap.get(username);
    }

    @Override
    public void saveUser(String username, String password) {
        logger.warn("The [Properties Datasource] currently does not support creating users.");
    }
}
