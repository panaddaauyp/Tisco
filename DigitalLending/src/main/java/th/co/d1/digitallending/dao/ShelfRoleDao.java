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
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfRole;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

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
    public List<ShelfRole> getShelfRoleListUserMA(String dbEnv) {
        List<ShelfRole> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfRole.class);
            Integer[] statusArr = {218,225};
            criteria.add(Restrictions.not(Restrictions.in("status", statusArr)));
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

    public ShelfRole saveShelfRole(String dbEnv, ShelfRole role, String username) {
        Transaction trans = null;
        try {
            Session session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.saveOrUpdate(role);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
        }
        return role;
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
    
    public JSONArray getShelfRoleMenuFuncList(String dbEnv) throws SQLException{
        JSONArray list = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List params = new ArrayList<>();
            StringBuilder cmd = new StringBuilder();
            cmd.append("SELECT RM.UUID UUIDRM, RM.ROLE_UUID ROLEUUID, RM.MENU_UUID MENUUUID, RM.STATUS STATUS, RM.ATTR10 STATUS_APPROVE, RF.UUID UUIDRF, RF.F_CREATE FCREATE, RF.F_EDIT FEDIT, "
            + "RF.F_DELETE FDELETE, RF.F_PREVIEW FPREVIEW, RF.F_EXPORT FEXPORT, RF.F_APPROVE FAPPROVE, "
            + "RF.F_TERMINATE FTERMINATE, RF.F_PAUSE FPAUSE, RF.F_START FSTART "
            + "FROM T_SHELF_ROLE_FUNC RF, T_SHELF_ROLE_MENU RM "
            + "WHERE RF.ROLE_MENU_ID = RM.UUID AND RM.STATUS != 225 order by RF.CREATE_AT DESC ");
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
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject()
                          .put("uuidrm", ValidUtils.null2NoData(rs.getString("UUIDRM")))
                          .put("status", ValidUtils.null2NoData(rs.getString("STATUS")))
                          .put("statusapprove", ValidUtils.null2NoData(rs.getString("STATUS_APPROVE")))
                          .put("roleuuid", ValidUtils.null2NoData(rs.getString("ROLEUUID")))
                          .put("menuuuid", ValidUtils.null2NoData(rs.getString("MENUUUID")));
                JSONObject func = new JSONObject()
                          .put("uuidrf", ValidUtils.null2NoData(rs.getString("UUIDRF")))
                          .put("create", ValidUtils.null2NoData(rs.getString("FCREATE")))
                          .put("edit", ValidUtils.null2NoData(rs.getString("FEDIT")))
                          .put("delete", ValidUtils.null2NoData(rs.getString("FDELETE")))
                          .put("preview", ValidUtils.null2NoData(rs.getString("FPREVIEW")))
                          .put("export", ValidUtils.null2NoData(rs.getString("FEXPORT")))
                          .put("approve", ValidUtils.null2NoData(rs.getString("FAPPROVE")))
                          .put("terminate", ValidUtils.null2NoData(rs.getString("FTERMINATE")))
                          .put("pause", ValidUtils.null2NoData(rs.getString("FPAUSE")))
                          .put("start", ValidUtils.null2NoData(rs.getString("FSTART")));
                obj.put("function",func);
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
    
      public JSONObject updateByRoleID(String dbEnv, String roleID ,String username,int status) {
        Transaction trans = null;
        PreparedStatement ps = null;
        Date sysdate = new Date();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder cmd = new StringBuilder();

            cmd.append("UPDATE T_SHELF_ROLE A SET "
                      + "STATUS = "+status+","
                      + "UPDATE_AT = '" + sysdate.toString() +"', "
                      + "UPDATE_BY = '" + username +"', "
                      + "ATTR10 = "+status+", "
                      + "ATTR9 = CONCAT(A.ATTR9,'/',"+status+") "
                      + "where a.uuid = '" + roleID +"' ");
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
