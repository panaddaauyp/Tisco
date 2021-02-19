/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.util.logging.Logger;
import org.hibernate.HibernateException;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;
import java.util.Properties;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.*;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author MEMEI
 */
public class ConfigUtils {

    final static Logger logger = Logger.getLogger(StatusUtils.class.getName());
    private static Properties properties = new Properties();
    private static Boolean useEnv = null;
    private static Map<String, String> env = null;

    public static String getConfig(String key) {
        try {
            
            if(useEnv == null){
                String resourceName = "config.properties";
                // properties = new Properties();
                ClassLoader loader = Thread.currentThread().getContextClassLoader();
                try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
                    properties.load(resourceStream);
                } catch (IOException ex) {
                    Logger.getLogger(ConfigUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
                useEnv = Boolean.parseBoolean(properties.getProperty("use_env"));
                if(Boolean.TRUE.equals(useEnv)){
                    env = System.getenv();
                }
            }
            String result = null;
            if(Boolean.TRUE.equals(useEnv)){
                result = env.get(key);
            } else {
                result = properties.getProperty(key);
            }
            // logger.info("-==================== useEnv >>>>>>>>>>>>: " + useEnv);
            // logger.info("-=======>>>>>>>========>>>>>>>===== key: " + key + ", value->" + result);
            return result;
        } catch (Exception e) {
            Logger.getLogger(ConfigUtils.class.getName()).log(Level.SEVERE, null, e);
            logger.info(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
