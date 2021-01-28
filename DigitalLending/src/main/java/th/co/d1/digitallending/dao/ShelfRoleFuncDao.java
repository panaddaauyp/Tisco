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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 22-10-2020 1:50:49 PM
 */
public class ShelfRoleFuncDao {

    Logger logger = Logger.getLogger(ShelfRoleFuncDao.class.getName());

    public ShelfRoleFunc getShelfRoleFunc(String dbEnv, String uuid) {
        ShelfRoleFunc menu = new ShelfRoleFunc();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            menu = (ShelfRoleFunc) session.get(ShelfRoleFunc.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return menu;
    }

    public ShelfRoleFunc getShelfRoleFuncByRoleMenuId(String dbEnv, String roleMenuUuid) {
        ShelfRoleFunc roleFunc = null;
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleFunc.class);
            criteria.add(Restrictions.eq("roleMenuId.uuid", roleMenuUuid));
            List<ShelfRoleFunc> list = criteria.list();
            trans.commit();
            if (null != list && list.size() > 0) {
                roleFunc = list.get(0);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return roleFunc;
    }

    public ShelfRoleFunc saveShelfRoleFunction(String dbEnv, ShelfRoleFunc list, String username) {
        Transaction trans = null;
        try {
            Session session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.saveOrUpdate(list);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
        }
        return list;
    }

    public JSONObject updateByRoleMenuUuid(String dbEnv, String listRoleMenu, String username, int status) {
        Transaction trans = null;
        PreparedStatement ps = null;
        Date sysdate = new Date();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder cmd = new StringBuilder();
            cmd.append("UPDATE T_SHELF_ROLE_FUNC A SET "
                    + "STATUS = " + status + " ,"
                    + "UPDATE_AT = '" + sysdate.toString() + "', "
                    + "UPDATE_BY = '" + username + "', "
                    + "ATTR10 = " + status + " ,"
                    + "ATTR9 = CONCAT(A.ATTR9,'/'," + status + " ) "
                    + "where a.role_menu_id in (" + listRoleMenu + ") ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());

            ps.executeUpdate();
            trans.commit();
            ps.close();
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
