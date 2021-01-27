/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfTmpAttach;

import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfTmpAttDao {

    private Session session;
    Logger logger = Logger.getLogger(ShelfTmpAttDao.class);

    public List<ShelfTmpAttach> getListShelfTmpAttach(String dbEnv) {
        List<ShelfTmpAttach> shelfTmpAttach = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpAttach.class);
            criteria.add(Restrictions.eq("status", 1));
            shelfTmpAttach = criteria.list();
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return shelfTmpAttach;
    }

    public JSONObject saveShelfTmpAttach(Session session, ShelfTmpAttach shelfTmpAttach) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.save(shelfTmpAttach);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            trans.rollback();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateShelfTmpAttach(Session session, ShelfTmpAttach shelfTmpAttach) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.update(shelfTmpAttach);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            trans.rollback();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateStatus(String dbEnv, String attUuid, int status) {
        Transaction trans = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("update ShelfTmpAttach set status = ? Where uuid = ?");
            ps = con.prepareStatement(prodCmd.toString());
            ps.setInt(1, status);
            ps.setString(2, attUuid);
            ps.executeUpdate();
            trans.commit();
            ps.close();
            session.close();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException | SQLException e) {
            logger.error("" + e);
            e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (null != session) {
                    session.close();
                }
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error("" + ex);
            }
        }
    }
}
