package com.riteny.simpleauth;

import com.riteny.simpleauth.datasource.UserDatasource;
import com.riteny.simpleauth.entity.SimpleAuthUser;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;

import javax.security.auth.login.LoginException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleAuthService {

    private final static Map<String, SimpleAuthUser> users = new ConcurrentHashMap<>();

    private final UserDatasource userDatasource;

    public SimpleAuthService(UserDatasource userDatasource) {
        this.userDatasource = userDatasource;
    }

    /**
     * 注冊用戶
     *
     * @param username 用戶名
     * @param password 密碼
     */
    public void register(String username, String password) {

        SimpleAuthUser simpleAuthUser = userDatasource.searchUserByUsername(username);
        if (simpleAuthUser != null) {
            throw new RuntimeException("User already exists with username: " + username);
        }

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            byte[] bytes = messageDigest.digest(password.getBytes());

            String passwordMd5 = Hex.encodeHexString(bytes);

            userDatasource.saveUser(username, passwordMd5);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 用戶登錄
     *
     * @param username 用戶名
     * @param password 密碼
     * @return 登錄令牌
     */
    public String login(String username, String password) throws LoginException {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            throw new LoginException("Username or password is empty");
        }

        SimpleAuthUser simpleAuthUser = userDatasource.searchUserByUsernameAndPassword(username, password);
        if (simpleAuthUser == null) {
            throw new LoginException("Username or password is incorrect");
        }

        //清除該用戶的其他登錄狀態
        resetUserLoginStatus(username);

        String token = UUID.randomUUID().toString().replace("-", "");
        users.put(token, simpleAuthUser);

        return token;
    }

    /**
     * 用戶登錄，並檢查該登錄的時間是否距離處理時間過長，如果過長，則該次登錄數據無效
     *
     * @param username    用戶名
     * @param md5AuthData 密碼
     * @param timestamp   登錄時的時間戳
     * @return 用戶信息
     */
    public String login(String username, String md5AuthData, Long timestamp) throws LoginException {

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(md5AuthData)) {
            throw new LoginException("Username or password is empty");
        }

        if ((System.currentTimeMillis() - timestamp) > 5 * 60 * 1000) {
            throw new LoginException("Verify data expired, please try again");
        }

        SimpleAuthUser simpleAuthUser = userDatasource.searchUserByUsername(username);

        if (simpleAuthUser == null) {
            throw new LoginException("Username or password is incorrect");
        }

        String userPassword = simpleAuthUser.getPassword();
        String verifyData = getMd5Str(userPassword + timestamp);

        if (!verifyData.equals(md5AuthData)) {
            throw new LoginException("Username or password is incorrect or verify data expired, please try again ");
        }

        //清除該用戶的其他登錄狀態
        resetUserLoginStatus(username);

        String token = UUID.randomUUID().toString().replace("-", "");
        users.put(token, simpleAuthUser);

        return token;
    }

    /**
     * 注銷登錄狀態
     *
     * @param token 登錄令牌
     */
    public void logout(String token) {

        SimpleAuthUser simpleAuthUser = users.get(token);

        if (simpleAuthUser != null) {
            //清除該用戶的其他登錄狀態
            resetUserLoginStatus(simpleAuthUser.getUsername());
            users.remove(token);
        }
    }

    /**
     * 讀取登錄令牌内容
     *
     * @param token 登錄令牌
     * @return 用戶信息
     */
    public SimpleAuthUser readToken(String token) throws LoginException {

        SimpleAuthUser simpleAuthUser = users.get(token);

        if (simpleAuthUser == null) {
            throw new LoginException("Token is invalid .");
        }

        return simpleAuthUser;
    }

    private String getMd5Str(String data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            byte[] bytes = messageDigest.digest(data.getBytes());

            return Hex.encodeHexString(bytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void resetUserLoginStatus(String username) {
        users.forEach((key, value) -> {
            if (username.equals(value.getUsername())) {
                users.remove(key);
            }
        });
    }
}
