/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfRole;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 05-05-2020 4:08:43 PM
 */
public class ShelfRoleDao {

    Logger logger = Logger.getLogger(ShelfRoleDao.class.getName());

    public List<ShelfRole> getShelfRoles(String dbEnv, String[] role, Integer status) {
        List<ShelfRole> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<String> roles = Arrays.asList(role);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRole.class);
            if (null != roles && roles.size() > 0) {
                criteria.add(Restrictions.in("roleId", roles));
            }
            if (null != status && status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<ShelfRole> getShelfRoleList(String dbEnv, Integer status) {
        List<ShelfRole> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRole.class);
            if (null != status && status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public JSONObject saveShelfRole(String dbEnv, ShelfRole role, String username) {
        JSONObject resp = new JSONObject();
        Date sysdate = new Date();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            try {
                trans = session.beginTransaction();
                if (null == role.getCreateAt()) {
                    role.setCreateBy(username);
                    role.setCreateAt(sysdate);
                } else {
                    role.setUpdateBy(username);
                    role.setUpdateAt(sysdate);
                }
                session.saveOrUpdate(role);
                trans.commit();
                resp.put("status", true);
                resp.put("role", new JSONObject(role));
            } catch (Exception e) {
                if (null != trans) {
                    trans.rollback();
                }
                resp.put("status", false);
                resp.put("description", "" + e);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            resp.put("status", false);
            resp.put("description", "" + e);
        }
        return resp;
    }

    public ShelfRole getShelfRole(String dbEnv, String uuid) {
        ShelfRole role = new ShelfRole();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            role = (ShelfRole) session.get(ShelfRole.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return role;
    }
}
