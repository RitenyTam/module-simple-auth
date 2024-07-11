package com.riteny.simpleauth.datasource;

import com.riteny.simpleauth.entity.SimpleAuthUser;

public interface UserDatasource {

    /**
     * 根據用戶名和密碼查詢用戶
     *
     * @param username 用戶名
     * @param password 密碼
     * @return 用戶信息
     */
    SimpleAuthUser searchUserByUsernameAndPassword(String username, String password);

    /**
     * 根據用戶名查詢用戶
     *
     * @param username 用戶名
     * @return 用戶信息
     */
    SimpleAuthUser searchUserByUsername(String username);

    /**
     * 保存用戶
     *
     * @param username 用戶名
     * @param password 密碼
     */
    void saveUser(String username, String password);
}
