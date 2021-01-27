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
import java.util.Arrays;
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
import th.co.d1.digitallending.entity.ShelfMenu;
import th.co.d1.digitallending.entity.ShelfRoleFunc;
import th.co.d1.digitallending.entity.ShelfRoleMenu;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

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

    public List<ShelfRoleMenu> getRolesMenusByRoleID(String dbEnv, String[] role, Integer status) throws SQLException {
        List<ShelfRoleMenu> list = new ArrayList<>();
        JSONArray ret = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List params = new ArrayList<>();
            StringBuilder cmd = new StringBuilder();
            if (status != null) {
                cmd.append("SELECT * FROM T_SHELF_ROLE_MENU WHERE ROLE_UUID IN ( "
                          + "SELECT UUID FROM T_SHELF_ROLE WHERE STATUS = 213 ");
                if (null != role) {
                    cmd.append("AND ROLE_ID IN ( ? ");
                    params.add(role);
                    cmd.append(" ) )");
                }
            }
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
            if (params.size() > 0) {
                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i) instanceof String) {
                        ps.setString(i + 1, (String) params.get(i));
                    } else {
                        ps.setInt(i + 1, (Integer) params.get(i));
                    }
                }
            }
            System.out.println("-----" + ps);
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject().put("uuid", ValidUtils.null2NoData(rs.getString("UUID")));
//                        .put("seqNo", ValidUtils.null2NoData(rs.getString("SEQ_NO")))
//                        .put("compCode", ValidUtils.null2NoData(rs.getString("COMP_CODE")))
//                        .put("compName", ValidUtils.null2NoData(rs.getString("COMP_NAME")));
                ret.put(obj);
            }
            System.out.println("ret--" + ret);
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

//    public JSONObject saveShelfShelfRoleMenuFunction(String dbEnv, List<ShelfRoleMenu> list, String username) {
//        JSONObject resp = new JSONObject();
//        Transaction trans = null;
//        try (Session session = getSessionMaster(dbEnv).openSession()) {
//            Date sysdate = new Date();
//            trans = session.beginTransaction();
//            for (ShelfRoleMenu roleMenu : list) {
//                if (null == roleMenu.getCreateAt()) {
//                    roleMenu.setCreateAt(sysdate);
//                    roleMenu.setCreateBy(username);
//                } else {
//                    roleMenu.setUpdateAt(sysdate);
//                    roleMenu.setUpdateBy(username);
//                }
//                session.saveOrUpdate(roleMenu);
//                for (ShelfRoleFunc func : roleMenu.getShelfRoleFuncList()) {
//                    func.setRoleMenuId(roleMenu);
//                    if (null == roleMenu.getCreateAt()) {
//                        func.setCreateAt(sysdate);
//                        func.setCreateBy(username);
//                    } else {
//                        func.setUpdateAt(sysdate);
//                        func.setUpdateBy(username);
//                    }
//                    session.saveOrUpdate(func);
//                }
//            }
//            trans.commit();
//            resp.put("status", true);
//        } catch (HibernateException | NullPointerException e) {
//            logger.info(e.getMessage());
//            e.printStackTrace();
//            if (null != trans) {
//                trans.rollback();
//            }
//            resp.put("status", false);
//            resp.put("description", "" + e);
//        }
//        return resp;
//    }
    public ShelfRoleMenu saveShelfRoleMenu(String dbEnv, ShelfRoleMenu list, String username) {
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

    public List<ShelfRoleMenu> getRolesMenusByRoleUuid(String dbEnv, String roleUuid) {
        List<ShelfRoleMenu> list = new ArrayList<>();
        Transaction trans = null;
        int status = 225;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRoleMenu.class);
            if (null != roleUuid && roleUuid != "") {
                criteria.add(Restrictions.eq("roleUuid.uuid", roleUuid));
                criteria.add(Restrictions.ne("status", status));
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

    public JSONObject deleteRM(String dbEnv, String Uuid, String statusApprove, String username) {
        Transaction trans = null;
        PreparedStatement ps = null;
        Date sysdate = new Date();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder cmd = new StringBuilder();
            cmd.append("UPDATE T_SHELF_ROLE_MENU SET UPDATE_AT = ? , UPDATE_BY = ?, ATTR9 = ?, ATTR10 = ? WHERE UUID = ?");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
            ps.setString(1, sysdate.toString());
            ps.setString(2, username);
            ps.setString(3, statusApprove);
            ps.setString(4, statusApprove);
            ps.setString(5, Uuid);
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

    public JSONObject updateByRoleID(String dbEnv, String roleID ,String username,int status) {
        Transaction trans = null;
        PreparedStatement ps = null;
        Date sysdate = new Date();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder cmd = new StringBuilder();
            cmd.append("UPDATE T_SHELF_ROLE_MENU A SET "
                      + "STATUS = "+status+" ,"
                      + "UPDATE_AT = '" + sysdate.toString() +"', "
                      + "UPDATE_BY = '" + username +"', "
                      + "ATTR10 = "+status+", "
                      + "ATTR9 = CONCAT(A.ATTR9,'/',"+status+") "
                      + "where a.role_uuid = '" + roleID +"' ");
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
    
    public JSONArray geByRoleID(String dbEnv, String roleID ,String username) throws SQLException{
        JSONArray list = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List params = new ArrayList<>();
            StringBuilder cmd = new StringBuilder();
            cmd.append("select uuid " +
                "from t_shelf_role_menu a " +
                "where a.role_uuid = '"+roleID+"' " +
                "AND a.status != 213");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(cmd.toString());
           
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject().put("uuid", ValidUtils.null2NoData(rs.getString("uuid")));
                list.put(obj);
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
        return list;
    }
}
