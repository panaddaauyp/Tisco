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
import th.co.d1.digitallending.entity.ShelfMenu;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import th.co.d1.digitallending.entity.ShelfRoleMenu;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

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

    Logger logger = Logger.getLogger(ShelfRoleMenuDao.class.getName());

    public List<ShelfRoleMenu> getShelfRoleMenus(String dbEnv, String roleId, Integer status) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
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
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<ShelfRoleMenu> getShelfRolesMenus(String dbEnv, String[] role, Integer status) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<String> roles = Arrays.asList(role);
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
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public List<ShelfRoleMenu> getActionShelfRolesMenus(String dbEnv, String[] role, String menuCode) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<String> roles = Arrays.asList(role);
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
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return list;
    }

    public ShelfRoleMenu getShelfRoleMenu(String dbEnv, String uuid) {
        ShelfRoleMenu role = new ShelfRoleMenu();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            role = (ShelfRoleMenu) session.get(ShelfRoleMenu.class, uuid);
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

    public JSONObject saveShelfShelfRoleMenuFunction(String dbEnv, List<ShelfRoleMenu> list, String username) {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Date sysdate = new Date();
            trans = session.beginTransaction();
            for (ShelfRoleMenu roleMenu : list) {
                if (null == roleMenu.getCreateAt()) {
                    roleMenu.setCreateAt(sysdate);
                    roleMenu.setCreateBy(username);
                } else {
                    roleMenu.setUpdateAt(sysdate);
                    roleMenu.setUpdateBy(username);
                }
                session.saveOrUpdate(roleMenu);
                for (ShelfRoleFunc func : roleMenu.getShelfRoleFuncList()) {
                    func.setRoleMenuId(roleMenu);
                    if (null == roleMenu.getCreateAt()) {
                        func.setCreateAt(sysdate);
                        func.setCreateBy(username);
                    } else {
                        func.setUpdateAt(sysdate);
                        func.setUpdateBy(username);
                    }
                    session.saveOrUpdate(func);
                }
            }
            trans.commit();
            resp.put("status", true);
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            resp.put("status", false);
            resp.put("description", "" + e);
        }
        return resp;
    }

    public List<ShelfRoleMenu> getShelfRoleByRoleMenu(String dbEnv, String roleUid, String menuUid, Integer status) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleMenu.class);
            if (null != roleUid && !roleUid.isEmpty()) {
                criteria.createAlias("roleUuid", "role");
                criteria.add(Restrictions.eq("role.uuid", roleUid));
            }
            if (null != status && status > 0) {
                criteria.add(Restrictions.eq("status", status));
            }
            if (null != menuUid && !menuUid.isEmpty()) {
                criteria.createAlias("menuUuid", "menu");
                criteria.add(Restrictions.eq("menu.uuid", menuUid));
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
}
