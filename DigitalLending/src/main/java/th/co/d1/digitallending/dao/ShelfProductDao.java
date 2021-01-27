/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.util.HibernateUtil;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

public class ShelfProductDao {

    Logger logger = Logger.getLogger(ShelfCompDtlDao.class);
    private Session session;

    public List<ShelfProduct> getListShelfProduct(String dbEnv) {
        List<ShelfProduct> shelfProductList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProduct> cr = cb.createQuery(ShelfProduct.class);
            Root<ShelfProduct> root = cr.from(ShelfProduct.class);
            cr.select(root);
            cr.orderBy(cb.asc(root.get("prodName")));
            Query<ShelfProduct> prodCmd = session.createQuery(cr);
            shelfProductList = prodCmd.getResultList();
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
        return shelfProductList;
    }

    public ShelfProduct getShelfProductByUUID(String dbEnv, String uuid) {
        ShelfProduct shelfProduct = new ShelfProduct();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfProduct = (ShelfProduct) session.get(ShelfProduct.class, uuid);
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
        return shelfProduct;
    }

    public ShelfProduct createShelfProduct(String dbEnv, ShelfProduct shelfProduct) throws HibernateException, NullPointerException {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            List<ShelfProductVcs> list = shelfProduct.getShelfProductVcsList();
            if (null == shelfProduct.getCreateAt()) {
                shelfProduct.setCreateAt(new Date());
            }
            session.save(shelfProduct);
            if (null != list) {
                for (ShelfProductVcs vcs : list) {
                    vcs.setProdUuid(shelfProduct);
                    List<ShelfProductDtl> list2 = vcs.getShelfProductDtlList();
                    if (null == vcs.getCreateAt()) {
                        vcs.setCreateAt(new Date());
                    }
                    session.save(vcs);
                    if (null != list2) {
                        for (ShelfProductDtl dtl : list2) {
                            dtl.setTrnUuid(vcs);
                            if (null == dtl.getCreateAt()) {
                                dtl.setCreateAt(new Date());
                            }
                            session.save(dtl);
                        }
                    }
                }
            }
            trans.commit();
            session.close();
            return shelfProduct;
        } catch (HibernateException | NullPointerException e) {
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
            return null;
        } finally {
            if (null != session) {
                session.close();
            }
        }
    }

    public ShelfProduct updateShelfProduct(String dbEnv, ShelfProduct shelfProduct) throws HibernateException, NullPointerException {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.saveOrUpdate(shelfProduct);
            List<ShelfProductVcs> list = shelfProduct.getShelfProductVcsList();
            if (null != list) {
                for (ShelfProductVcs vcs : list) {
                    vcs.setProdUuid(shelfProduct);
                    List<ShelfProductDtl> list2 = vcs.getShelfProductDtlList();
                    session.saveOrUpdate(vcs);
                    if (null != list2) {
                        for (ShelfProductDtl dtl : list2) {
                            dtl.setTrnUuid(vcs);
                            session.saveOrUpdate(dtl);
                        }
                    }
                }
            }
            trans.commit();
            session.close();
            return shelfProduct;
        } catch (HibernateException | NullPointerException e) {
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
            return null;
        } finally {
            if (null != session) {
                session.close();
            }
        }
    }

    public List<ShelfProduct> getShelfProductByUuidCode(String dbEnv, String uuid, String prodCode) {
        List<ShelfProduct> prods = new ArrayList<>();
        Transaction trans;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProduct.class);
            if (null != uuid && !"".equals(uuid)) {
                criteria.add(Restrictions.eq("uuid", uuid));
            }
            if (null != prodCode && !"".equals(prodCode)) {
                criteria.add(Restrictions.eq("prodCode", prodCode));
            }
            prods = criteria.list();
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
        return prods;
    }

    public JSONObject updateProduct(String dbEnv, String prodCode, String compName, String lkCode, String lkValue) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "");
        Connection con = null;
        PreparedStatement ps = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder prodCmd = new StringBuilder();
            if ("INITIAL PRODUCT".equalsIgnoreCase(compName)) {
                prodCmd.append("update t_shelf_product_dtl  "
                        + " set lk_value = ? "
                        + " WHERE uuid = (select dtl.uuid"
                        + " from t_shelf_product_dtl dtl, "
                        + "	t_shelf_product_vcs vcs,"
                        + "	t_shelf_product sp"
                        + " where 1=1"
                        + " and vcs.uuid = dtl.trn_uuid"
                        + " and vcs.comp_uuid is null"
                        + " and dtl.lk_code = ?"
                        + " and sp.uuid = vcs.prod_uuid"
                        + " and sp.prod_code = ?)");
            } else {
                prodCmd.append("update t_shelf_product_dtl  "
                        + " set lk_value = ? "
                        + " WHERE uuid = (select dtl.uuid"
                        + " from t_shelf_product_dtl dtl, "
                        + "	t_shelf_product_vcs vcs,"
                        + "	t_shelf_comp comp,"
                        + "	t_shelf_product sp"
                        + " where 1=1"
                        + " and vcs.uuid = dtl.trn_uuid"
                        + " and vcs.comp_uuid = comp.uuid"
                        + " and comp.comp_name = ?"
                        + " and dtl.lk_code = ?"
                        + " and sp.uuid = vcs.prod_uuid"
                        + " and sp.prod_code = ?)");
            }
            ps = con.prepareStatement(prodCmd.toString());
            if ("INITIAL PRODUCT".equalsIgnoreCase(compName)) {
                ps.setString(1, lkValue);
                ps.setString(2, lkCode);
                ps.setString(3, prodCode);
            } else {
                ps.setString(1, lkValue);
                ps.setString(2, compName);
                ps.setString(3, lkCode);
                ps.setString(4, prodCode);
            }
            ps.executeUpdate();
            con.commit();
            ps.close();
//            connection.close();
//            System.out.println("result : " + result);
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            resp.put("status", 500).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (null != session) {
                    session.close();
                }
                if (!con.isClosed()) {
                    con.close();
                }
            } catch (SQLException ex) {
                logger.error("" + ex);
            }
        }
        return resp;
    }

    public List<ShelfProduct> getListShelfProductOnShelf(String dbEnv) {
        List<ShelfProduct> shelfProductList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProduct> cr = cb.createQuery(ShelfProduct.class);
            Root<ShelfProduct> root = cr.from(ShelfProduct.class);
            cr.select(root).where(cb.equal(root.get("attr2"), "Y"));
            cr.orderBy(cb.asc(root.get("prodName")));
            Query<ShelfProduct> prodCmd = session.createQuery(cr);
            shelfProductList = prodCmd.getResultList();
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
        return shelfProductList;
    }

    public JSONObject updateAllData(String dbEnv, JSONObject obj) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "");
        Connection con = null;
        PreparedStatement ps = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(obj.getString("data")));
            String dataCmd = new String(decodedBytes);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append(dataCmd);
            ps = con.prepareStatement(prodCmd.toString());
            resp.put("data", ps.executeUpdate());
            con.commit();
            ps.close();
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            resp.put("status", 500).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (null != session) {
                    session.close();
                }
//                if (null != con && !con.isClosed()) {
//                    con.close();
//                }
            } catch (SQLException ex) {
                logger.error("" + ex);
            }
        }
        return resp;
    }

    public HashMap getListActiveProductByStatus(String dbEnv, Integer status) {
        Connection con = null;
        HashMap hmp = new HashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append("SELECT vcs.PROD_UUID AS PROD_UUID,DTL.uuid AS DTL_UUID, DTL.LK_CODE AS LK_CODE, DTL.LK_VALUE AS LK_VALUE, vcs.VER_PROD AS VER_PROD "
                    + " FROM T_SHELF_PRODUCT_VCS vcs , T_SHELF_PRODUCT_DTL DTL "
                    + " WHERE 1=1 "
                    + " AND vcs.UUID = DTL.TRN_UUID"
                    + " AND vcs.COMP_UUID IS NULL"
                    + " AND DTL.LK_CODE IN ('activeDate','endDate')"
                    + " AND vcs.STATUS = ? ");
            ps = con.prepareStatement(taskCategoryCmd.toString());
            ps.setInt(1, status);
            rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = ValidUtils.null2NoData(rs.getString("PROD_UUID"));
                String dtlUuid = ValidUtils.null2NoData(rs.getString("PROD_UUID"));
                String ver = ValidUtils.null2NoData(rs.getInt("VER_PROD"));
                String lkCode = ValidUtils.null2NoData(rs.getString("LK_CODE"));
                String lkValue = ValidUtils.null2NoData(rs.getString("LK_VALUE"));
                JSONObject data = new JSONObject();
                data.put("uuid", uuid)
                        .put("dtlUuid", dtlUuid)
                        .put("ver", ver)
                        .put("activeDate", "")
                        .put("endDate", "");
                String kMap = uuid;//+ "_" + ver;
                if (null != hmp.get(kMap)) {
                    data = (JSONObject) hmp.get(kMap);
                    if ("activeDate".equalsIgnoreCase(lkCode)) {
                        data.put("activeDate", lkValue);
                    }
                    if ("endDate".equalsIgnoreCase(lkCode)) {
                        data.put("endDate", lkValue);
                    }
                } else {
                    if ("activeDate".equalsIgnoreCase(lkCode)) {
                        data.put("activeDate", lkValue);
                    }
                    if ("endDate".equalsIgnoreCase(lkCode)) {
                        data.put("endDate", lkValue);
                    }
                }
                hmp.put(kMap, data);
            }
            con.commit();
            rs.close();
            ps.close();
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            try {
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
        return hmp;
    }

    public JSONObject getAllData(String dbEnv, JSONObject obj) {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "");
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            JSONArray arr = new JSONArray();
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(obj.getString("data")));
            String dataCmd = new String(decodedBytes);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append(dataCmd);
            ps = con.prepareStatement(prodCmd.toString());
            rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject tmp = new JSONObject();
                ResultSetMetaData rsMeta = rs.getMetaData();
                int colNumbers = rsMeta.getColumnCount();
                for (int i = 1; i <= colNumbers; i++) {
                    tmp.put(rsMeta.getColumnLabel(i), ValidUtils.null2NoData(rs.getString(i)));
                }
                arr.put(tmp);
            }
            resp.put("datas", arr);
            con.commit();
            ps.close();
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            resp.put("status", 500).put("description", "" + e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (null != session) {
                    session.close();
                }
//                if (null != con && !con.isClosed()) {
//                    con.close();
//                }
            } catch (SQLException ex) {
                logger.error("" + ex);
            }
        }
        return resp;
    }
}
