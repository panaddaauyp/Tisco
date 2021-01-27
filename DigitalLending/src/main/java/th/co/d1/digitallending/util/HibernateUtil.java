package th.co.d1.digitallending.util;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.json.JSONException;
import org.json.JSONObject;

public class HibernateUtil {

    private static StandardServiceRegistry serviceRegistry;
    public static Map<String, SessionFactory> master = buildSessionFactoryMaster();
    public static SessionFactory mem = buildSessionFactoryMems();
    public static final String defaultDB = "default";
//    private static final String ROOT_PATH = "C:/";
//    private static final String ROOT_PATH = "/opt";
/*
    public static Map<String, SessionFactory> buildSessionFactoryMaster() {
        Map<String, SessionFactory> mSessionFactory = new HashMap<>();
        try {
            String fileName = "db.json";
//            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

//            File file = new File(classLoader.getResource(fileName).getFile());
            //Read File Content
//            String content = new String(Files.readAllBytes(file.toPath()));
            URL fileResource = Resources.getResource(fileName);
            String content = Resources.toString(fileResource, StandardCharsets.UTF_8);
            JSONObject dtObj = new JSONObject(content);
            for (String dbEnvVal : dtObj.keySet()) {
                JSONObject db = dtObj.getJSONObject(dbEnvVal);
                String dbName = db.getString("database_name");
                String username = db.getString("secretname");
                String pDb = db.getString("region");
                if (dbName != null && !dbName.isEmpty()) {
//                    String url = String.format("jdbc:postgresql://db.d1asia.co.th:5432/%s", dbName);
                    String url = String.format("jdbc:postgresql://172.24.20.90:5432/%s", dbName);
//                    String url = String.format("jdbc:postgresql://localhost:5432/%s", dbName);
                    Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
                    StandardServiceRegistryBuilder standardServiceRegistryBuilder = cfg.getStandardServiceRegistryBuilder();
                    standardServiceRegistryBuilder.applySetting("hibernate.connection.url", url);
                    standardServiceRegistryBuilder.applySetting("hibernate.connection.username", username);
                    standardServiceRegistryBuilder.applySetting("hibernate.connection.password", pDb);
                    serviceRegistry = standardServiceRegistryBuilder.build();
                    Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
                    mSessionFactory.put(dbEnvVal, metadata.getSessionFactoryBuilder().build());
                }
            }
            return mSessionFactory;
        } catch (IOException | HibernateException | JSONException ex) {
            ex.printStackTrace();
            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
            }
            throw new ExceptionInInitializerError(ex);
        }
    }
     */

    private static Map<String, SessionFactory> buildSessionFactoryMaster() {
        Map<String, SessionFactory> mSessionFactory = new HashMap<>();
        try {
            String fileName = "db.json";
//            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//
//            File file = new File(classLoader.getResource(fileName).getFile());
//            String content = new String(Files.readAllBytes(file.toPath()));
            //Read File Content
            URL fileResource = Resources.getResource(fileName);
            String content = Resources.toString(fileResource, StandardCharsets.UTF_8);
            JSONObject dtObj = new JSONObject(content);

            for (String dbEnvVal : dtObj.keySet()) {
                JSONObject db = dtObj.getJSONObject(dbEnvVal);
                String database_name = db.getString("database_name");
                String secretName = db.getString("secretname");
                String region = db.getString("region");
                if (database_name != null && !database_name.isEmpty()) {
//            String secretName = "alpha/digitalshelf";
//            String region = "ap-southeast-1";
                    AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard()
                            .withRegion(region).build();

                    GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretName);

                    GetSecretValueResult getSecretValueResult = client.getSecretValue(getSecretValueRequest);
                    final String secretBinaryString = getSecretValueResult.getSecretString();
                    final ObjectMapper objectMapper = new ObjectMapper();
                    final HashMap<String, String> secretMap = objectMapper.readValue(secretBinaryString, HashMap.class);

                    String url = String.format("jdbc:postgresql://%s:%s/%s", secretMap.get("host"), secretMap.get("port"), database_name);
//                    String dbUsr = secretMap.get("username");
//                    String dbPwd = secretMap.get("password");
                    Configuration cfg = new Configuration().configure("hibernate.cfg.xml");
                    StandardServiceRegistryBuilder standardServiceRegistryBuilder = cfg.getStandardServiceRegistryBuilder();
                    standardServiceRegistryBuilder.applySetting("hibernate.connection.url", url);
                    standardServiceRegistryBuilder.applySetting("hibernate.connection.username", secretMap.get("username"));
                    standardServiceRegistryBuilder.applySetting("hibernate.connection.password", secretMap.get("password"));
                    serviceRegistry = standardServiceRegistryBuilder.build();
                    Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
                    mSessionFactory.put(dbEnvVal, metadata.getSessionFactoryBuilder().build());
                }
            }
            return mSessionFactory;
        } catch (IOException | HibernateException | JSONException ex) {
            ex.printStackTrace();
            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
            }
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionMaster(String dbEnv) {
        Properties properties = new Properties();
        String resourceName = "config.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            properties.load(resourceStream);
        } catch (IOException ex) {
            Logger.getLogger(HibernateUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (Boolean.parseBoolean(properties.getProperty("use_state"))) {
            if (dbEnv == null || dbEnv.isEmpty()) {
                dbEnv = HibernateUtil.defaultDB;
            } else if (HibernateUtil.master.get(dbEnv) == null) {
                dbEnv = HibernateUtil.defaultDB;
            }
        } else {
            dbEnv = HibernateUtil.defaultDB;
        }
        return master.get(dbEnv);
    }

    private static SessionFactory buildSessionFactoryMems() {
        try {
            Configuration cfg = new Configuration().configure("hibernate.mem.cfg.xml");
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = cfg.getStandardServiceRegistryBuilder();
            /*
            String usernameToDecrypt = cfg.getProperty("hibernate.connection.username");
            EncryptAndDecrypt dao = new EncryptAndDecrypt();
            if (usernameToDecrypt.startsWith("ENC(") && usernameToDecrypt.endsWith(")")) {
                usernameToDecrypt = usernameToDecrypt.substring(4, usernameToDecrypt.length() - 1);
                String decrypted = dao.deCodeValue(usernameToDecrypt);
//                System.out.println("Username -> " + decrypted);
                standardServiceRegistryBuilder.applySetting("hibernate.connection.username", decrypted);
            }

            String passwordToDecrypt = cfg.getProperty("hibernate.connection.password");
            if (passwordToDecrypt.startsWith("ENC(") && passwordToDecrypt.endsWith(")")) {
                passwordToDecrypt = passwordToDecrypt.substring(4, passwordToDecrypt.length() - 1);
                String decrypted = dao.deCodeValue(passwordToDecrypt);
//                System.out.println("decrypted -> "+decrypted);
                standardServiceRegistryBuilder.applySetting("hibernate.connection.password", decrypted);
            }
             */
            serviceRegistry = standardServiceRegistryBuilder.build();
            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            return metadata.getSessionFactoryBuilder().build();

        } catch (HibernateException ex) {

            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
            }

            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionMem() {
        return mem;
    }

    public static void shutdown() {
        // Close caches and connection pools
        // getSessionFactory().close();
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }
}
