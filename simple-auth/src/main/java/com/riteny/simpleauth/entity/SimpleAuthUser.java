package com.riteny.simpleauth.entity;

import lombok.Data;

import java.util.Date;

@Data
public class SimpleAuthUser {

    private String username;

    private String password;

    private String createdBy;

    private Date createdTime;
}
