package com.dwang;

import com.dwang.config.MyBatisSessionFactory;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author dwang
 * @version 1.0.0
 * @Title
 * @ClassName MapperProxy.java
 * @Description 创建 Mapper 动态代理工具类
 * @createTime 2025-11-23 20:59
 */

public class MapperProxy {

    @SuppressWarnings("unchecked")
    public static <T> T create(final Class<T> mapperClass) {
        return (T) Proxy.newProxyInstance(
                mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                new MapperInvocationHandler<>(mapperClass)
        );
    }

    private static class MapperInvocationHandler<T> implements InvocationHandler {
        private final Class<T> mapperClass;

        public MapperInvocationHandler(Class<T> mapperClass) {
            this.mapperClass = mapperClass;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try (SqlSession session = MyBatisSessionFactory.getSession()) {
                T mapper = session.getMapper(mapperClass);
                Object result = method.invoke(mapper, args);

                // 如果是 insert / update / delete，自动提交事务
                String methodName = method.getName().toLowerCase();
                if (methodName.startsWith("insert") ||
                        methodName.startsWith("update") ||
                        methodName.startsWith("delete")) {
                    session.commit();
                }
                return result;
            } catch (Exception e) {
                throw e;
            }
        }

    }
}
