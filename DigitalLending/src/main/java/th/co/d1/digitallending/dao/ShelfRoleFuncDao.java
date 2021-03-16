/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

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

    public ShelfRoleFunc getShelfRoleFuncByRoleMenuId(String dbEnv, String roleMenuId) {
        ShelfRoleFunc roleFunc = null;
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleFunc.class);
            criteria.add(Restrictions.eq("roleMenuId.uuid", roleMenuId));
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

}
