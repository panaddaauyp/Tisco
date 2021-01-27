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
import java.util.logging.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
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

    Logger logger = Logger.getLogger(UtilityDao.class.getName());

    public String getCutOffProduct(String dbEnv, String prodId, Integer verProd) throws SQLException {
        String cutoff = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append("SELECT LK_CODE, LK_LABEL, LK_VALUE FROM T_SHELF_PRODUCT_DTL "
                    + "WHERE LK_CODE IN ('pcutOffTAllD','pcutOffTSpec') "
                    + "AND TRN_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT_VCS WHERE PROD_UUID = ? AND VER_PROD = ?) ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(taskCategoryCmd.toString());
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
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return cutoff;
    }
}
