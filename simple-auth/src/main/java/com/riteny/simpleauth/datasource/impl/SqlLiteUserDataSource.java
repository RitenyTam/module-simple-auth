package com.riteny.simpleauth.datasource.impl;

import com.riteny.simpleauth.datasource.UserDatasource;
import com.riteny.simpleauth.entity.SimpleAuthUser;

import java.net.URL;
import java.sql.*;

public class SqlLiteUserDataSource implements UserDatasource {

    private String url;

    private static final String searchUserByUsernameAndPasswordSql = "select * from user where username=? and password=?";
    private static final String searchUserByUsernameSql = "select * from user where username=?";
    private static final String saveUserSql = "insert into user( username,password) values(?,?)";

    {
        URL fileUrl = this.getClass().getClassLoader().getResource("file:identifier.sqlite");
        URL classpathUrl = this.getClass().getClassLoader().getResource("identifier.sqlite");

        if (fileUrl == null && classpathUrl == null) {
            throw new RuntimeException("identifier.sqlite not found");
        } else if (fileUrl == null) {
            url = "jdbc:sqlite:" + classpathUrl.getFile();
        } else {
            url = "jdbc:sqlite:" + fileUrl.getFile();
        }
    }

    public SqlLiteUserDataSource(String dbFilePath) {
//        url = dbFilePath;
    }

    @Override
    public SimpleAuthUser searchUserByUsernameAndPassword(String username, String password) {
        return handlerDatabaseOperation(connection -> {

            try (PreparedStatement statement = connection.prepareStatement(searchUserByUsernameAndPasswordSql)) {

                statement.setString(1, username);
                statement.setString(2, password);

                return excuteQueryAndReturnSimpleAuthUser(statement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public SimpleAuthUser searchUserByUsername(String username) {
        return handlerDatabaseOperation(connection -> {

            try (PreparedStatement statement = connection.prepareStatement(searchUserByUsernameSql)) {

                statement.setString(1, username);

                return excuteQueryAndReturnSimpleAuthUser(statement);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Override
    public void saveUser(String username, String password) {

        handlerDatabaseOperation((Handler<String>) connection -> {

            try (PreparedStatement statement = connection.prepareStatement(saveUserSql)) {

                statement.setString(1, username);
                statement.setString(2, password);

                statement.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return null;
        });
    }

    private SimpleAuthUser excuteQueryAndReturnSimpleAuthUser(PreparedStatement statement) throws SQLException {

        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            String usernameResult = rs.getString("username");
            String passwordResult = rs.getString("password");

            SimpleAuthUser simpleAuthUser = new SimpleAuthUser();
            simpleAuthUser.setUsername(usernameResult);
            simpleAuthUser.setPassword(passwordResult);

            return simpleAuthUser;
        } else {
            return null;
        }
    }


    private <T> T handlerDatabaseOperation(Handler<T> handler) {

        //SQLite 数据库文件
        try (Connection conn = DriverManager.getConnection(url)) {
            Class.forName("org.sqlite.JDBC");
            return handler.handle(conn);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    interface Handler<T> {
        T handle(Connection connection);
    }
}
