/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import th.co.d1.digitallending.entity.ShelfRoleMenu;
import th.co.d1.digitallending.util.HibernateUtil;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 1023 MS Siam Tower, 30th Floor, Rama
 * 3 Rd., Chongnonsi, Yannawa, Bangkok, 10120
 *
 * @create 06-03-2020 9:37:34 AM
 */
public class ShelfRoleMenuDao {

    private Session session;

    Logger logger = Logger.getLogger(ShelfRoleMenuDao.class);

    public List<ShelfRoleMenu> getShelfRoleMenus(String dbEnv, String roleId, Integer status) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleMenu.class);
            if (null != roleId && !"".equals(roleId)) {
                criteria.createAlias("roleUuid", "role");
                criteria.add(Restrictions.eq("role.roleId", roleId));
            }
            if (null != status && status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            list = criteria.list();
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
        return list;
    }

    public List<ShelfRoleMenu> getShelfRolesMenus(String dbEnv, String[] role, Integer status) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans;
        try {
            List<String> roles = Arrays.asList(role);
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleMenu.class);
            if (null != roles && roles.size() > 0) {
                criteria.createAlias("roleUuid", "role");
                criteria.add(Restrictions.in("role.roleId", roles));
            }
            if (null != status && status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            list = criteria.list();
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
        return list;
    }
    
    public List<ShelfRoleMenu> getActionShelfRolesMenus(String dbEnv, String[] role, String menuCode) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans;
        try {
            List<String> roles = Arrays.asList(role);
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleMenu.class);
            if (null != roles && roles.size() > 0) {
                criteria.createAlias("roleUuid", "role");
                criteria.add(Restrictions.in("role.roleId", roles));
            }
            if (!"".equals(menuCode)) {
                criteria.createAlias("menuUuid", "menu");
                criteria.add(Restrictions.eq("menu.menuCode", menuCode));
            }
            list = criteria.list();
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
        return list;
    }
}
