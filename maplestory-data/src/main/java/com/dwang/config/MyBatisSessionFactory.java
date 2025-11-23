package com.dwang.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.Reader;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName MyBatisSessionFactory.java
 * @Description TODO
 * @createTime 2025-11-23 20:14
 */

public class MyBatisSessionFactory {
    private static SqlSessionFactory factory;

    static {
        try {
            Reader reader = Resources.getResourceAsReader("mybatis-config.xml");
            factory = new SqlSessionFactoryBuilder().build(reader);
            factory.getConfiguration().setEnvironment(
                    new Environment("dev", new JdbcTransactionFactory(), DataSources.dataSource())
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static SqlSession getSession() {
        return factory.openSession();
    }
}
