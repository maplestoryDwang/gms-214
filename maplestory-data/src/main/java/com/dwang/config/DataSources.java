package com.dwang.config;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName DataSources.java
 * @Description 数据库配置
 * @createTime 2025-11-23 20:14
 */

public class DataSources {
    private static DataSource dataSource;

    public static DataSource dataSource() {
        if (dataSource == null) {
            MysqlDataSource ds = new MysqlDataSource();
            ds.setURL("jdbc:mysql://127.0.0.1:3308/v214?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC");
            ds.setUser("root");
            ds.setPassword("123456");
            dataSource = ds;
        }
        return dataSource;
    }
}
