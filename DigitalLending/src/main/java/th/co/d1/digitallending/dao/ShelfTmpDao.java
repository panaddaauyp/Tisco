/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfTmp;

import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfTmpDao {

    private Session session;
    Logger logger = Logger.getLogger(ShelfTmpDao.class);

    public List<ShelfTmp> getListByStatus(String dbEnv, int status) {
        List<ShelfTmp> shelfTmp = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmp.class);
            criteria.add(Restrictions.eq("status", status));
            criteria.addOrder(Order.asc("tmpName"));
            shelfTmp = criteria.list();
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
        return shelfTmp;
    }

    public JSONObject saveTmp(Session session, ShelfTmp shelfTmp) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.save(shelfTmp);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            trans.rollback();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateTmp(Session session, ShelfTmp shelfTmp) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.update(shelfTmp);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            trans.rollback();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateStatus(String dbEnv, String tmpUuid, int status) {
        Transaction trans = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("update t_shelf_tmp set status = ? Where uuid = ?");
            ps = con.prepareStatement(prodCmd.toString());
            ps.setInt(1, status);
            ps.setString(2, tmpUuid);
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
//                if (!con.isClosed()) {
//                    con.close();
//                }
            } catch (SQLException ex) {
                logger.error("" + ex);
            }
        }
    }

    public ShelfTmp getShelfTmp(String dbEnv, String uuid) {
        ShelfTmp theme = new ShelfTmp();
        Transaction trans = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            theme = (ShelfTmp) session.get(ShelfTmp.class, uuid);
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return theme;
    }

    public JSONArray getTemplateListAndProductUsage(String dbEnv) {
        Connection con = null;
        JSONArray ret = new JSONArray();
        PreparedStatement ps = null, psOperLog = null;
        ResultSet rs = null, prodListRs = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder reconcileCmd = new StringBuilder();
            reconcileCmd.append("SELECT * FROM T_SHELF_TMP");
            ps = con.prepareStatement(reconcileCmd.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject tmpObj = new JSONObject();
                tmpObj.put("uuid", rs.getString("uuid"));
                tmpObj.put("tmpName", rs.getString("tmp_name"));
                tmpObj.put("value", rs.getString("value"));
                tmpObj.put("currentVcsUuid", rs.getString("current_vcs_uuid"));
                tmpObj.put("previousVcsUuid", rs.getString("previous_vcs_uuid"));
                tmpObj.put("attr1", rs.getString("attr1"));
                tmpObj.put("attr2", rs.getString("attr2"));
                tmpObj.put("attr3", rs.getString("attr3"));
                tmpObj.put("attr4", rs.getString("attr4"));
                tmpObj.put("attr5", rs.getString("attr5"));
                tmpObj.put("attr6", rs.getString("attr6"));
                tmpObj.put("attr7", rs.getString("attr7"));
                tmpObj.put("attr8", rs.getString("attr8"));
                tmpObj.put("attr9", rs.getString("attr9"));
                tmpObj.put("attr10", rs.getString("attr10"));
                tmpObj.put("description", rs.getString("description"));
                tmpObj.put("status", rs.getString("status"));
                tmpObj.put("create_at", rs.getTimestamp("create_at"));
                tmpObj.put("create_by", rs.getString("create_by"));
                tmpObj.put("update_at", rs.getTimestamp("update_at"));
                tmpObj.put("update_by", rs.getString("update_by"));
                tmpObj.put("company_code", rs.getString("company_code"));
                tmpObj.put("bussiness_dept", rs.getString("bussiness_dept"));
                tmpObj.put("business_line", rs.getString("business_line"));
                reconcileCmd.setLength(0);
                reconcileCmd.append("SELECT SP.PROD_CODE PROD_CODE, SP.PROD_NAME PROD_NAME, SP.BUSINESS_LINE BUSINESS_LINE, SP.BUSINESS_DEPT BUSINESS_DEPT, SP.COMPANY COMPANY, DTL.LK_CODE LK_CODE , DTL.LK_VALUE PRODTYPE ");
                reconcileCmd.append("FROM T_SHELF_PRODUCT SP, T_SHELF_PRODUCT_VCS VCS , T_SHELF_PRODUCT_DTL DTL ");
                reconcileCmd.append("WHERE 1=1 ");
                reconcileCmd.append("AND VCS.PROD_UUID= SP.UUID ");
                reconcileCmd.append("AND VCS.COMP_UUID IS NULL ");
                reconcileCmd.append("AND DTL.TRN_UUID = VCS.UUID ");
                reconcileCmd.append("AND DTL.LK_CODE = 'prodType' ");
                reconcileCmd.append("AND VCS.TEM_UUID = ?");
                psOperLog = con.prepareStatement(reconcileCmd.toString());
                psOperLog.setString(1, tmpObj.getString("uuid"));
                prodListRs = psOperLog.executeQuery();
                JSONArray prodArr = new JSONArray();
                while (prodListRs.next()) {
                    JSONObject prodObj = new JSONObject();
                    prodObj.put("productCode", prodListRs.getString("PROD_CODE"));
                    prodObj.put("productName", prodListRs.getString("PROD_NAME"));
                    prodObj.put("businessLine", prodListRs.getString("BUSINESS_LINE"));
                    prodObj.put("businessDept", prodListRs.getString("BUSINESS_DEPT"));
                    prodObj.put("company", prodListRs.getString("COMPANY"));
                    prodObj.put("productType", prodListRs.getString("PRODTYPE"));
                    prodArr.put(prodObj);
                }
                tmpObj.put("product", prodArr);
                ret.put(tmpObj);
            }
            con.commit();
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            try {
                if (prodListRs != null) {
                    prodListRs.close();
                }
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (NullPointerException | SQLException ex) {
                logger.error("" + ex);
            }
            if (null != session) {
                session.close();
            }
        }
        return ret;
    }
}
