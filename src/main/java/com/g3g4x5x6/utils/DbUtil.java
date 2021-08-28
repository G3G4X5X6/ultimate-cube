package com.g3g4x5x6.utils;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;


@Slf4j
public class DbUtil {
    private DbUtil() {

    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        String dbPath = System.getProperties().getProperty("user.home") + "/.ultimateshell/ultilmateshell.sqlite";
        return DriverManager.getConnection("jdbc:sqlite:" + dbPath);
    }

    public static Boolean updateAccessTime(long time, String where) {
        try {
            Connection connection = DbUtil.getConnection();
            Statement statement = connection.createStatement();

            String sql = "UPDATE session SET access_time='" + time + "' WHERE " + where;

            int result = statement.executeUpdate(sql);
            log.debug("更新访问时间返回码：" + result);
            if (result >= 1) {
                return true;
            }
            DbUtil.close(connection, statement);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
        return true;
    }

    public static void close(Connection connection, Statement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

    }

    public static void close(Connection connection, Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
