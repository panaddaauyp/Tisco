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
import java.util.logging.Logger;
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

    Logger logger = Logger.getLogger(ShelfTmpAttDao.class.getName());

    public List<ShelfTmpAttach> getListShelfTmpAttach(String dbEnv) {
        List<ShelfTmpAttach> shelfTmpAttach = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpAttach.class);
            criteria.add(Restrictions.eq("status", 1));
            shelfTmpAttach = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
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
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (trans != null) {
                trans.rollback();
            }
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
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (trans != null) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateStatus(String dbEnv, String attUuid, int status) {
        Transaction trans = null;
        PreparedStatement ps = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("update ShelfTmpAttach set status = ? Where uuid = ?");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
            ps.setInt(1, status);
            ps.setString(2, attUuid);
            ps.executeUpdate();
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException | SQLException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            return new JSONObject().put("status", false).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException ex) {
                logger.info(ex.getMessage());
            }
        }
    }
}
