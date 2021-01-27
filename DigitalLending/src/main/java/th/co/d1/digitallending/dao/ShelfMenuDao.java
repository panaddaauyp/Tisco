/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfMenu;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 21-10-2020 12:56:12 PM
 */
public class ShelfMenuDao {

    Logger logger = Logger.getLogger(ShelfMenuDao.class.getName());

    public List<ShelfMenu> getShelfMenuList(String dbEnv, Integer status) {
        List<ShelfMenu> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfMenu.class);
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

    public JSONObject saveShelfMenu(String dbEnv, ShelfMenu menu, String username) {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Date sysdate = new Date();
            try {
                trans = session.beginTransaction();
                if (null == menu.getCreateAt()) {
                    menu.setCreateBy(username);
                    menu.setCreateAt(sysdate);
                } else {
                    menu.setUpdateBy(username);
                    menu.setUpdateAt(sysdate);
                }
                session.saveOrUpdate(menu);
                trans.commit();
                resp.put("status", true);
                resp.put("menu", new JSONObject(menu));
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

    public ShelfMenu getShelfMenu(String dbEnv, String uuid) {
        ShelfMenu menu = new ShelfMenu();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            menu = (ShelfMenu) session.get(ShelfMenu.class, uuid);
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

}
