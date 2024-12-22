package net.swordie.ms.util;

import net.swordie.ms.handlers.FieldHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;

/**
 * @author 橘子
 * @version 1.0.0
 * @ClassName CustomConfigsLoad.java
 * @Description 加载自定义配置
 * @createTime 2024-12-22 14:05
 */

public class CustomConfigsLoad {

    static Logger log = LogManager.getLogger(CustomConfigsLoad.class);

    private static Properties properties = new Properties();

    public static void load() {

        // 通过类加载器加载配置文件
        try (InputStream input = CustomConfigsLoad.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                log.error("Sorry, unable to find config.properties");
                return;
            }

            // 加载配置文件
            properties.load(input);
            Set<Object> objects = properties.keySet();
            for (Object object : objects) {
                log.debug("load param: {}",  object);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public static String getConfig(String key) {
        return properties.getProperty(key);
    }


}
