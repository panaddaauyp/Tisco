/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.json.JSONArray;
import org.json.JSONObject;
import static th.co.d1.digitallending.dao.SysOperLogDao.logger;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 08-05-2020 10:14:50 AM
 */
public class UtilityDao {

    private Session session;
    Logger logger = Logger.getLogger(UtilityDao.class);

    public String getCutOffProduct(String dbEnv, String prodId, Integer verProd) {
        Connection con = null;
        String cutoff = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append("SELECT LK_CODE, LK_LABEL, LK_VALUE FROM T_SHELF_PRODUCT_DTL "
                    + "WHERE LK_CODE IN ('pcutOffTAllD','pcutOffTSpec') "
                    + "AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) ");
            ps = con.prepareStatement(taskCategoryCmd.toString());
            ps.setString(1, prodId);
            ps.setInt(2, verProd);
            rs = ps.executeQuery();
            String pcutOffTAllD = "", pcutOffTSpec = "";
            while (rs.next()) {
                if ("pcutOffTAllD".equalsIgnoreCase(rs.getString("LK_CODE"))) {
                    pcutOffTAllD = ValidUtils.null2NoData(rs.getString("LK_VALUE"));
                } else if ("pcutOffTSpec".equalsIgnoreCase(rs.getString("LK_CODE"))) {
                    pcutOffTSpec = ValidUtils.null2NoData(rs.getString("LK_VALUE"));
                }
            }
            if ("all".equalsIgnoreCase(pcutOffTAllD)) {
                cutoff = "All Day";
            } else {
                cutoff = pcutOffTSpec;
            }
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (NullPointerException | SQLException ex) {
                logger.error("" + ex);
            }
            if (null != session) {
                session.close();
            }
        }
        return cutoff;
    }
}
