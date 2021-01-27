/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import com.tfglog.LogSingleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.h2.tools.Server;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.SysLookup;

/**
 *
 * @author Poomsakul Senakul
 */
public class ConstainValue {

    private final String memDBName = "constainDB";
    private final String JDBC_DRIVER_MEM = "org.h2.Driver";
    private final String URL = "jdbc:h2:mem:" + memDBName + ";DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS TEST";
    private final String USER = "sa";
    private final String PASS = "NhTpHEG4";
//    public void loadH2Database() {
//        try {
//            createMemDb();
//            setLookUp();
//
//        } catch (Exception ex) {
////            log4j.error(ex);
//        }
//    }

    public Server createMemDb() throws ClassNotFoundException {
        Server server = null;
        System.out.println("-----------------------------------------------------------------------");
        Class.forName(JDBC_DRIVER_MEM);
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USER, PASS);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT 'Memory Database is Online'");
            if (rs.next()) {
//                System.out.println(" >>>>>>>> " + rs.getString(1) + " <<<<<<<< ");
            }
            if (server != null) {
                server.shutdown();
                server.stop();
            } else {
                server = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "8891", "-ifExists").start();
//                System.out.println("URL: jdbc:h2:" + server.getURL() + "/mem:" + memDBName);
            }
            con.close();
        } catch (SQLException ex) {
//            System.out.println("Can't not create : " + ex);
//            log4j.error(ex);
        } finally {
//            System.out.println("-----------------------------------------------------------------------");
            try {
                if(!con.isClosed()){
                    con.close();
                }
            } catch (NullPointerException | SQLException ex) {
                Logger.getLogger(ConstainValue.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return server;
    }

    public Connection getConnectionMem() throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException ex) {
            if (conn == null) {
                throw new IllegalStateException("Database connection was not initialized");
            }
        }
        return conn;
    }

    public void InitialTiscoLog() {
        HashMap<String, Object> prop = new HashMap<>();
        prop.put("ENGINE_NAME", "BackendShelf");
        prop.put("RESPONSE_CODE_PATH", "status");
        prop.put("RESPONSE_DESC_PATH", "description");
        try {
            LogSingleton.initial(prop);
            LogSingleton.SetLogToConsole(true); //default = false
            LogSingleton.SetLogLevel(LogSingleton.LogLevel.ALL); //default = LogLevel.ALL, possible values are [LogLevel.ALL, LogLevel.INFO, LogLevel.DEBUG, LogLevel.ERROR]
        } catch (Exception ex) {
//            java.util.logging.Logger.getLogger(ApplicationStartup.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void setLookUp() {
        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName(JDBC_DRIVER_MEM);
            conn = getConnectionMem();
            stmt = conn.createStatement();

//            System.out.println("Creating table H2 memLookUp");
            String lookupCmd = "CREATE TABLE IF NOT EXISTS memLookUp ("
                    + "id VARCHAR(128), "
                    + "uuid VARCHAR(128), "
                    + "lookupCode VARCHAR(255), "
                    + "lookupNameTh VARCHAR(255), "
                    + "lookupNameEn VARCHAR(255), "
                    + "lookupType VARCHAR(255), "
                    + "lookupValue VARCHAR(255), "
                    + "attr1 LONGVARCHAR, "
                    + "attr2 LONGVARCHAR, "
                    + "attr3 LONGVARCHAR, "
                    + "attr4 LONGVARCHAR, "
                    + "attr5 LONGVARCHAR, "
                    + "attr6 LONGVARCHAR, "
                    + "attr7 LONGVARCHAR, "
                    + "attr8 LONGVARCHAR, "
                    + "attr9 LONGVARCHAR, "
                    + "attr10 LONGVARCHAR, "
                    + "flagEdit BOOLEAN, "
                    + "flagCreate BOOLEAN, "
                    + "status int, "
                    + "description LONGVARCHAR, "
                    + "PRIMARY KEY (id))";
            stmt.executeUpdate(lookupCmd);
            lookupCmd = "TRUNCATE TABLE memLookUp";
            stmt.executeUpdate(lookupCmd);

            for (String dbEnv : new HibernateUtil().master.keySet()) {
                List<SysLookup> getLookupList = new SysLookupDao().getListLookup(dbEnv);
//                JSONObject jsonObj = new JSONObject();
                for (SysLookup list : getLookupList) {
                    lookupCmd = "INSERT INTO memLookUp VALUES ("
                            + "'" + UUID.randomUUID().toString() + "',"
                            + "'" + list.getUuid() + "',"
                            + "'" + list.getLookupCode() + "',"
                            + "'" + list.getLookupNameTh() + "',"
                            + "'" + list.getLookupNameEn() + "',"
                            + "'" + list.getLookupType() + "',"
                            + "'" + list.getLookupValue() + "',"
                            + "'" + list.getAttr1() + "',"
                            + "'" + list.getAttr2() + "',"
                            + "'" + list.getAttr3() + "',"
                            + "'" + list.getAttr4() + "',"
                            + "'" + list.getAttr5() + "',"
                            + "'" + list.getAttr6() + "',"
                            + "'" + list.getAttr7() + "',"
                            + "'" + list.getAttr8() + "',"
                            + "'" + list.getAttr9() + "',"
                            + "'" + dbEnv + "',"
                            + "'" + list.getFlagEdit() + "',"
                            + "'" + list.getFlagCreate() + "',"
                            + "'" + list.getStatus() + "',"
                            + "'" + list.getDescription() + "'"
                            + ")";
                    stmt.executeUpdate(lookupCmd);
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            //Handle errors for JDBC
            e.printStackTrace();
        } //Handle errors for Class.forName
        finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            } //end finally try
        } //end try
    }

    public int updateLookUp() {
        Connection conn = null;
        Statement stmt = null;
        int ret = 1;
        try {
            Class.forName(JDBC_DRIVER_MEM);
            conn = getConnectionMem();
            stmt = conn.createStatement();

//            System.out.println("Updating table H2 memLookUp");
            String memLookupCmd = "TRUNCATE TABLE memLookUp";
            stmt.executeUpdate(memLookupCmd);

            for (String dbEnv : new HibernateUtil().master.keySet()) {
                List<SysLookup> getLookupList = new SysLookupDao().getListLookup(dbEnv);
//                JSONObject jsonObj = new JSONObject();
                for (SysLookup list : getLookupList) {
                    memLookupCmd = "INSERT INTO memLookUp VALUES ("
                            + "'" + UUID.randomUUID().toString() + "',"
                            + "'" + list.getUuid() + "',"
                            + "'" + list.getLookupCode() + "',"
                            + "'" + list.getLookupNameTh() + "',"
                            + "'" + list.getLookupNameEn() + "',"
                            + "'" + list.getLookupType() + "',"
                            + "'" + list.getLookupValue() + "',"
                            + "'" + list.getAttr1() + "',"
                            + "'" + list.getAttr2() + "',"
                            + "'" + list.getAttr3() + "',"
                            + "'" + list.getAttr4() + "',"
                            + "'" + list.getAttr5() + "',"
                            + "'" + list.getAttr6() + "',"
                            + "'" + list.getAttr7() + "',"
                            + "'" + list.getAttr8() + "',"
                            + "'" + list.getAttr9() + "',"
                            + "'" + dbEnv + "',"
                            + "'" + list.getFlagEdit() + "',"
                            + "'" + list.getFlagCreate() + "',"
                            + "'" + list.getStatus() + "',"
                            + "'" + list.getDescription() + "'"
                            + ")";
                    stmt.executeUpdate(memLookupCmd);
                }
            }
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            //Handle errors for JDBC
            e.printStackTrace();
            ret = 0;
        } //Handle errors for Class.forName
        finally {
            //finally block used to close resources
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
                ret = 0;
            } //end finally try
        } //end try
        return ret;
    }
}
