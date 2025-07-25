package com.bittercode.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.StoreException;

public class DBUtil {
    private DBUtil() {
    throw new UnsupportedOperationException("Utility class");
}

    static {
        try {
            Class.forName(DatabaseConfig.DRIVER_NAME);
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static Connection getConnection() throws StoreException {
        try {
            return DriverManager.getConnection(
                DatabaseConfig.CONNECTION_STRING,
                DatabaseConfig.DB_USER_NAME,
                DatabaseConfig.DB_PASSWORD
            );
        } catch (SQLException e) {
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
    }
}
