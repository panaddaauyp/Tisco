/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import com.google.gson.Gson;
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
import java.util.logging.Logger;
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
import th.co.d1.digitallending.entity.ShelfTheme;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.Utils;
import th.co.d1.digitallending.util.ValidUtils;

public class ShelfProductDao {

    Logger logger = Logger.getLogger(ShelfCompDtlDao.class.getName());

    public List<ShelfProduct> getListShelfProduct(String dbEnv) {
        List<ShelfProduct> shelfProductList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProduct> cr = cb.createQuery(ShelfProduct.class);
            Root<ShelfProduct> root = cr.from(ShelfProduct.class);
            cr.select(root);
            cr.orderBy(cb.asc(root.get("prodName")));
            Query<ShelfProduct> prodCmd = session.createQuery(cr);
            shelfProductList = prodCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductList;
    }

    public ShelfProduct getShelfProductByUUID(String dbEnv, String uuid) {
        ShelfProduct shelfProduct = new ShelfProduct();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfProduct = (ShelfProduct) session.get(ShelfProduct.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProduct;
    }

    public ShelfProduct createShelfProduct(String dbEnv, ShelfProduct shelfProduct)
            throws HibernateException, NullPointerException {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
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
            return shelfProduct;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

    public JSONArray updateShelfProduct(String dbEnv, ShelfProduct shelfProduct, String username) {
        Transaction trans = null;
        JSONArray ret = new JSONArray();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Date sysdate = new Date();
            trans = session.beginTransaction();
            shelfProduct.setUpdateAt(sysdate);
            shelfProduct.setUpdateBy(username);
            session.saveOrUpdate(shelfProduct);
            List<ShelfProductVcs> list = shelfProduct.getShelfProductVcsList();
            if (null != list) {
                for (ShelfProductVcs vcs : list) {
                    vcs.setProdUuid(shelfProduct);
                    List<ShelfProductDtl> list2 = vcs.getShelfProductDtlList();
                    vcs.setUpdateAt(sysdate);
                    vcs.setUpdateBy(username);
                    session.saveOrUpdate(vcs);
                    if (null != list2) {
                        for (ShelfProductDtl dtl : list2) {
                            dtl.setTrnUuid(vcs);
                            dtl.setUpdateAt(sysdate);
                            dtl.setUpdateBy(username);
                            session.saveOrUpdate(dtl);
//                            if (dtl.getLkCode().equalsIgnoreCase("imgSplashList") || dtl.getLkCode().equalsIgnoreCase("pdfUpload") || dtl.getLkCode().equalsIgnoreCase("imgUpload")) {
//                                JSONObject obj = new JSONObject();
//                                obj.put("uuid", dtl.getUuid());
//                                obj.put("lkCode", dtl.getLkCode());
//                                JSONArray dtlVal = new JSONArray();
//                                if (!dtl.getLkValue().isEmpty()) {
//                                    dtlVal = new JSONArray(dtl.getLkValue());
//                                    for (int i = 0; i < dtlVal.length(); i++) {
//                                        dtlVal.getJSONObject(i).remove("value");
//                                    }
//                                }
//                                obj.put("lkValue", dtlVal);
//                                obj.put("vcsUuid", dtl.getTrnUuid().getUuid());
//                                obj.put("prodUuid", dtl.getTrnUuid().getProdUuid().getUuid());
//                                obj.put("verProd", dtl.getTrnUuid().getVerProd());
//                                obj.put("compId", dtl.getTrnUuid().getCompUuid().getUuid());
//                                obj.put("compCode", dtl.getTrnUuid().getCompUuid().getCompCode());
//                                obj.put("compName", dtl.getTrnUuid().getCompUuid().getCompName());
//                                ret.put(obj);
//                            }
                        }
                    }
                }
            }
            ret.put(new JSONObject().put("uuid", shelfProduct.getUuid()));
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            // e.printStackTrace();
            ret = new JSONArray();
            throw e;
        }
        return ret;
    }

    public List<ShelfProduct> getShelfProductByUuidCode(String dbEnv, String uuid, String prodCode) {
        List<ShelfProduct> prods = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
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
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return prods;
    }

    public JSONObject updateProduct(String dbEnv, String prodCode, String compName, String lkCode, String lkValue)
            throws SQLException {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "");
        PreparedStatement ps = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder prodCmd = new StringBuilder();
            if ("INITIAL PRODUCT".equalsIgnoreCase(compName)) {
                prodCmd.append("update t_shelf_product_dtl  " + " set lk_value = ? " + " WHERE uuid = (select dtl.uuid"
                        + " from t_shelf_product_dtl dtl, " + "	t_shelf_product_vcs vcs," + "	t_shelf_product sp"
                        + " where 1=1" + " and vcs.uuid = dtl.trn_uuid" + " and vcs.comp_uuid is null"
                        + " and dtl.lk_code = ?" + " and sp.uuid = vcs.prod_uuid" + " and sp.prod_code = ?)");
            } else {
                prodCmd.append("update t_shelf_product_dtl  " + " set lk_value = ? " + " WHERE uuid = (select dtl.uuid"
                        + " from t_shelf_product_dtl dtl, " + "	t_shelf_product_vcs vcs," + "	t_shelf_comp comp,"
                        + "	t_shelf_product sp" + " where 1=1" + " and vcs.uuid = dtl.trn_uuid"
                        + " and vcs.comp_uuid = comp.uuid" + " and comp.comp_name = ?" + " and dtl.lk_code = ?"
                        + " and sp.uuid = vcs.prod_uuid" + " and sp.prod_code = ?)");
            }
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
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
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            // e.printStackTrace();
            resp.put("status", 500).put("description", "" + e);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return resp;
    }

    public List<ShelfProduct> getListShelfProductOnShelf(String dbEnv) {
        List<ShelfProduct> shelfProductList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProduct> cr = cb.createQuery(ShelfProduct.class);
            Root<ShelfProduct> root = cr.from(ShelfProduct.class);
            cr.select(root).where(cb.equal(root.get("attr2"), "Y"));
            cr.orderBy(cb.asc(root.get("prodName")));
            Query<ShelfProduct> prodCmd = session.createQuery(cr);
            shelfProductList = prodCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductList;
    }

    public JSONObject updateAllData(String dbEnv, JSONObject obj) throws SQLException {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "");
        PreparedStatement ps = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(obj.getString("data")));
            String dataCmd = new String(decodedBytes);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append(dataCmd);
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
            resp.put("data", ps.executeUpdate());
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            // e.printStackTrace();
            resp.put("status", 500).put("description", "" + e);
            throw e;
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return resp;
    }

    public HashMap getListProductByStatus(String dbEnv, Integer status) throws SQLException {
        HashMap hmp = new HashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append(
                    "SELECT vcs.PROD_UUID AS PROD_UUID,DTL.uuid AS DTL_UUID, DTL.LK_CODE AS LK_CODE, DTL.LK_VALUE AS LK_VALUE, vcs.VER_PROD AS VER_PROD, vcs.STATE STATE  "
                    + " FROM T_SHELF_PRODUCT_VCS vcs , T_SHELF_PRODUCT_DTL DTL " + " WHERE 1=1 "
                    + " AND vcs.UUID = DTL.TRN_UUID" + " AND vcs.COMP_UUID IS NULL"
                    + " AND DTL.LK_CODE IN ('activeDate','endDate')" + " AND vcs.STATUS = ? ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(taskCategoryCmd.toString());
            ps.setInt(1, status);
            rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = ValidUtils.null2NoData(rs.getString("PROD_UUID"));
                String dtlUuid = ValidUtils.null2NoData(rs.getString("DTL_UUID"));
                String ver = ValidUtils.null2NoData(rs.getInt("VER_PROD"));
                String lkCode = ValidUtils.null2NoData(rs.getString("LK_CODE"));
                String lkValue = ValidUtils.null2NoData(rs.getString("LK_VALUE"));
                String state = ValidUtils.null2NoData(rs.getString("STATE"));
                JSONObject data = new JSONObject();
                data.put("uuid", uuid).put("dtlUuid", dtlUuid).put("ver", ver).put("activeDate", "").put("endDate", "")
                        .put("state", state);
                String kMap = uuid;// + "_" + ver;
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
        return hmp;
    }

    public JSONObject getAllData(String dbEnv, JSONObject obj) throws SQLException {
        JSONObject resp = new JSONObject().put("status", 200).put("description", "");
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            JSONArray arr = new JSONArray();
            byte[] decodedBytes = Base64.getDecoder().decode(ValidUtils.null2NoData(obj.getString("data")));
            String dataCmd = new String(decodedBytes);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append(dataCmd);
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
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
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            resp.put("status", 500).put("description", "" + e);
            throw e;
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
        return resp;
    }

    public List<JSONObject> searchProduct(String subState, String templateName, String productName, JSONArray status,
            String startActiveDate, String endActiveDate, String startUpdateDate, String endUpdateDate, String updateBy)
            throws SQLException {
        List<JSONObject> ret = new ArrayList<>();
        PreparedStatement ps = null, psDtl = null;
        ResultSet rs = null, rsDtl = null;
        try (Session session = getSessionMaster(subState).openSession()) {
            Integer statusActive = StatusUtils.getActive(subState).getStatusCode();
            Integer statusWaitDelete = StatusUtils.getWaittoDelete(subState).getStatusCode();
            Integer statusDelete = StatusUtils.getDelete(subState).getStatusCode();
            Integer statusTerminate = StatusUtils.getTerminate(subState).getStatusCode();
            List params = new ArrayList<>();
            StringBuilder searchProdCmd = new StringBuilder();
            searchProdCmd.append(
                    "SELECT SP.UUID,SP.PROD_CODE,SP.PROD_NAME, SP.BUSINESS_LINE, SP.BUSINESS_DEPT, SP.COMPANY,VCS.CREATE_AT,VCS.CREATE_BY,VCS.UPDATE_AT,VCS.UPDATE_BY, "
                    + " VCS.TEM_UUID,VCS.VER_TEM,VCS.THEME_UUID,VCS.VER_PROD,VCS.STATUS,LK.LOOKUP_NAME_TH STATUS_NAME_TH, LK.LOOKUP_NAME_EN STATUS_NAME_EN, "
                    + " TMP.TMP_NAME,THEME.THEME_CODE,THEME.THEME_NAME, "
                    + " DTL.LK_CODE,DTL.LK_VALUE,DTL.TRN_UUID, DTL.END_DATE " + " FROM T_SHELF_PRODUCT_VCS VCS "
                    + " INNER JOIN T_SHELF_PRODUCT SP ON VCS.PROD_UUID = SP.UUID "
                    + " INNER JOIN T_SHELF_PRODUCT_DTL DTL ON VCS.UUID = DTL.TRN_UUID "
                    + " INNER JOIN T_SHELF_TMP TMP ON VCS.TEM_UUID = TMP.UUID "
                    + " INNER JOIN T_SHELF_THEME THEME ON VCS.THEME_UUID = THEME.UUID "
                    + " INNER JOIN T_SYS_LOOKUP LK ON VCS.STATUS::text = LK.LOOKUP_CODE "
                    + " WHERE VCS.COMP_UUID IS NULL " + " AND VCS.STATUS <> 215 ");
            if (null != templateName && !templateName.isEmpty()) {
                searchProdCmd
                        .append(" AND VCS.TEM_UUID IN (SELECT UUID FROM T_SHELF_TMP WHERE LOWER(TMP_NAME) LIKE ?) ");
                params.add("%" + templateName.toLowerCase() + "%");
            }
            if (null != productName && !productName.isEmpty()) {
                searchProdCmd.append(
                        " AND VCS.PROD_UUID IN (SELECT UUID FROM T_SHELF_PRODUCT WHERE LOWER(PROD_NAME) LIKE ?) ");
                params.add("%" + productName.toLowerCase() + "%");
            }
            if (status.length() > 0) {
                searchProdCmd.append(" AND VCS.STATUS IN (? ");
                for (int i = 1; i < status.length(); i++) {
                    params.add(status.getInt(i - 1));
                    searchProdCmd.append(", ? ");
                }
                params.add(status.getInt(status.length() - 1));
                searchProdCmd.append(") ");
            }
            if ((null != startActiveDate && !startActiveDate.isEmpty())
                    && (null != endActiveDate && !endActiveDate.isEmpty())) {
                searchProdCmd.append(
                        " AND COALESCE(VCS.UPDATE_AT,VCS.CREATE_AT) BETWEEN TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS') AND TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startActiveDate + " " + "00:00:00");
                params.add(endActiveDate + " " + "23:59:59");
            } else if ((null == startActiveDate || startActiveDate.isEmpty())
                    && (null != endActiveDate && !endActiveDate.isEmpty())) {
                searchProdCmd.append(
                        " AND COALESCE(VCS.UPDATE_AT,VCS.CREATE_AT) <= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(endActiveDate + " " + "23:59:59");
            } else if ((null != startActiveDate && !startActiveDate.isEmpty())
                    && (null == endActiveDate || endActiveDate.isEmpty())) {
                searchProdCmd.append(
                        " AND COALESCE(VCS.UPDATE_AT,VCS.CREATE_AT) >= TO_TIMESTAMP(?, 'DD/MM/YYYY HH24:MI:SS')");
                params.add(startActiveDate + " " + "00:00:00");
            }
            searchProdCmd.append(" AND DTL.LK_CODE = 'activeDate' ");
            if ((null != startUpdateDate && !startUpdateDate.isEmpty())
                    && (null != endUpdateDate && !endUpdateDate.isEmpty())) {
                searchProdCmd
                        .append(" AND DTL.LK_VALUE BETWEEN TO_DATE(?, 'DD/MM/YYYY') AND TO_TIMESTAMP(?, 'DD/MM/YYYY')");
                params.add(startUpdateDate);
                params.add(endUpdateDate);
            } else if ((null == startUpdateDate || startUpdateDate.isEmpty())
                    && (null != endUpdateDate && !endUpdateDate.isEmpty())) {
                searchProdCmd.append(" AND DTL.LK_VALUE <= TO_DATE(?, 'DD/MM/YYYY')");
                params.add(endUpdateDate);
            } else if ((null != startUpdateDate && !startUpdateDate.isEmpty())
                    && (null == endUpdateDate || endUpdateDate.isEmpty())) {
                searchProdCmd.append(" AND DTL.LK_VALUE >= TO_DATE(?, 'DD/MM/YYYY')");
                params.add(startUpdateDate);
            }
            if (null != updateBy && !updateBy.isEmpty()) {
                searchProdCmd.append(" AND LOWER(COALESCE(VCS.UPDATE_BY,VCS.CREATE_BY))  LIKE ?");
                params.add("%" + updateBy.toLowerCase() + "%");
            }
            searchProdCmd.append(" ORDER BY VCS.UPDATE_AT DESC");
//            searchProdCmd.append(" ORDER BY SP.PROD_CODE, SP.PROD_NAME DESC");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(searchProdCmd.toString());
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
                boolean flagDel = true, flagEdit = true;
                JSONObject retObj = new JSONObject();
                JSONObject jdtl = new JSONObject();
                searchProdCmd.setLength(0);
                searchProdCmd.append(
                        "SELECT * FROM T_SHELF_PRODUCT_DTL DTL WHERE TRN_UUID = ?");
                psDtl = session.doReturningWork((Connection conn) -> conn).prepareStatement(searchProdCmd.toString());
                psDtl.setString(1, ValidUtils.null2NoData(rs.getString("TRN_UUID")));
                rsDtl = psDtl.executeQuery();
                while (rsDtl.next()) {
                    jdtl.put(ValidUtils.null2NoData(rsDtl.getString("LK_CODE")), ValidUtils.null2NoData(rsDtl.getString("LK_VALUE")));
                }
//                for (ShelfProductDtl dtl : vcs.getShelfProductDtlList()) {
//                    if (dtl.getStatus() == statusActive) {
//                        jdtl.put(dtl.getLkCode(), dtl.getLkValue());
//                    }
//                }
                jdtl.put("uuid", ValidUtils.null2NoData(rs.getString("UUID")));
                jdtl.put("prodId", ValidUtils.null2NoData(rs.getString("UUID")));
                jdtl.put("prodVer", ValidUtils.null2NoData(rs.getInt("VER_PROD")));
                jdtl.put("prodCode", ValidUtils.null2NoData(rs.getString("PROD_CODE")));
                jdtl.put("prodName", ValidUtils.null2NoData(rs.getString("PROD_NAME")));
                jdtl.put("businessLine", ValidUtils.null2NoData(rs.getString("BUSINESS_LINE")));
                jdtl.put("businessDept", ValidUtils.null2NoData(rs.getString("BUSINESS_DEPT")));
                jdtl.put("company", ValidUtils.null2NoData(rs.getString("COMPANY")));
                jdtl.put("status", ValidUtils.null2NoData(rs.getInt("STATUS")));
                jdtl.put("statusNameTh", ValidUtils.null2NoData(rs.getString("STATUS_NAME_TH")));
                jdtl.put("statusNameEn", ValidUtils.null2NoData(rs.getString("STATUS_NAME_EN")));
                jdtl.put("theme", ValidUtils.null2NoData(rs.getString("THEME_UUID")));
                jdtl.put("templateName", ValidUtils.null2NoData(rs.getString("TMP_NAME")));
                jdtl.put("verProd", ValidUtils.null2NoData(rs.getInt("VER_PROD")));
                jdtl.put("tmpVer", ValidUtils.null2NoData(rs.getInt("VER_TEM")));
                jdtl.put("createDate", DateUtils.getDisplayEnDate(rs.getTimestamp("CREATE_AT"), "yyyy-MM-dd HH:mm:ss"));
                if (statusWaitDelete.equals(ValidUtils.obj2BigInt(rs.getInt("STATUS"))) || statusDelete.equals(ValidUtils.obj2BigInt(rs.getInt("STATUS"))) || statusTerminate.equals(ValidUtils.obj2BigInt(rs.getInt("STATUS")))) {
                    flagEdit = false;
                    flagDel = false;
                } else if (statusActive.equals(ValidUtils.obj2BigInt(rs.getInt("STATUS")))) {
                    flagDel = false;
                }
                jdtl.put("edit", flagEdit);
                jdtl.put("delete", flagDel);
                jdtl.put("updateDate", ValidUtils.null2Separator(ValidUtils.null2NoData(DateUtils.getDisplayEnDate(rs.getTimestamp("UPDATE_AT"), "yyyy-MM-dd")), ValidUtils.null2NoData(DateUtils.getDisplayEnDate(rs.getTimestamp("CREATE_AT"), "yyyy-MM-dd"))));
                jdtl.put("updateBy", ValidUtils.null2NoData(rs.getString("UPDATE_BY")));
                jdtl.put("CreateBy", ValidUtils.null2NoData(rs.getString("CREATE_BY")));
                retObj.put("product", jdtl);
                JSONObject jtemplate = new JSONObject();
                ShelfTmpDao tmpDao = new ShelfTmpDao();
                ShelfTmp tmp = tmpDao.getShelfTmp(subState, ValidUtils.null2NoData(rs.getString("TEM_UUID")));
                if (null != tmp) {
                    jtemplate.put("uuid", tmp.getUuid());
                    jtemplate.put("name", tmp.getTmpName());
                    JSONArray jarray = new JSONArray();
                    for (ShelfTmpVcs tv : tmp.getShelfTmpVcsList()) {
                        if (tv.getStatus() == statusActive) {   //Confirm by Panadda 23/07/2020
                            jarray = new JSONArray(tv.getAttr1());
                        }
                    }
                    List<JSONObject> listTmpComp = new ArrayList<>();
                    for (int i = 0; i < jarray.length(); i++) {
                        listTmpComp.add(jarray.getJSONObject(i));
                    }
//                    Utils.sortJSONObjectByKey(listTmpComp, "seqNo", true);
                    jtemplate.put("component", jarray);
                }
                retObj.put("template", jtemplate);
                Gson gson = new Gson();
                ShelfTheme shelfTheme = new ShelfThemeDao().getShelfTmpTheme(updateBy, ValidUtils.null2NoData(rs.getString("THEME_UUID")));
                JSONObject json = new JSONObject(gson.toJson(null != shelfTheme ? shelfTheme.getValue() : new JSONObject()));
                retObj.put("theme", json.has("info") ? json.getJSONObject("info") : json);
                /*
                JSONObject obj = new JSONObject().put("prodUuid", ValidUtils.null2NoData(rs.getString("UUID")))
                        .put("prodCode", ValidUtils.null2NoData(rs.getString("PROD_CODE")))
                        .put("prodName", ValidUtils.null2NoData(rs.getString("PROD_NAME")))
                        .put("prodVer", ValidUtils.null2NoData(rs.getInt("VER_PROD")))
                        .put("prodStatus", ValidUtils.null2NoData(rs.getInt("STATUS")))
                        .put("prodStatusNameTh", ValidUtils.null2NoData(rs.getString("STATUS_NAME_TH")))
                        .put("prodStatusNameEn", ValidUtils.null2NoData(rs.getString("STATUS_NAME_EN")))
                        .put("prodCreateAt",
                                ValidUtils.null2NoData(
                                        DateUtils.getDisplayEnDate(rs.getTimestamp("CREATE_AT"), "yyyy-MM-dd")))
                        .put("prodCreateBy", ValidUtils.null2NoData(rs.getString("CREATE_BY")))
                        .put("prodUpdateAt",
                                ValidUtils.null2NoData(
                                        DateUtils.getDisplayEnDate(rs.getTimestamp("UPDATE_AT"), "yyyy-MM-dd")))
                        .put("prodUpdateBy", ValidUtils.null2NoData(rs.getString("UPDATE_BY")))
                        .put("templateUuid", ValidUtils.null2NoData(rs.getString("TEM_UUID")))
                        .put("templateVer", ValidUtils.null2NoData(rs.getInt("VER_TEM")))
                        .put("templateName", ValidUtils.null2NoData(rs.getString("TMP_NAME")))
                        .put("themeUuid", ValidUtils.null2NoData(rs.getString("THEME_UUID")))
                        .put("themeCode", ValidUtils.null2NoData(rs.getString("THEME_CODE")))
                        .put("themeName", ValidUtils.null2NoData(rs.getString("THEME_NAME")))
                        .put("activeDate", ValidUtils.null2NoData(rs.getString("LK_VALUE")))
                        .put("expireDate",
                                ValidUtils.null2NoData(
                                        DateUtils.getDisplayEnDate(rs.getTimestamp("END_DATE"), "yyyy-MM-dd")))
                        .put("endDate", "");
                searchProdCmd.setLength(0);
                searchProdCmd.append(
                        "SELECT LK_VALUE FROM T_SHELF_PRODUCT_DTL DTL WHERE DTL.LK_CODE = 'endDate' AND TRN_UUID = ?");
                psDtl = session.doReturningWork((Connection conn) -> conn).prepareStatement(searchProdCmd.toString());
                psDtl.setString(1, ValidUtils.null2NoData(rs.getString("TRN_UUID")));
                rsDtl = psDtl.executeQuery();
                while (rsDtl.next()) {
                    obj.put("endDate", ValidUtils.null2NoData(rsDtl.getString("LK_VALUE")));
                }
                ret.put(obj);
                 */
                ret.add(retObj);
                if (!rsDtl.isClosed()) {
                    rsDtl.close();
                }
                if (!psDtl.isClosed()) {
                    psDtl.close();
                }
            }
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rsDtl != null && !rsDtl.isClosed()) {
                rsDtl.close();
            }
            if (psDtl != null && !psDtl.isClosed()) {
                psDtl.close();
            }
            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
        }
//        Utils.sortJSONObjectByKey(ret, "updateDate", false);
        return ret;
    }

    public JSONArray listProductUse(String subState, String productCode) throws SQLException {
        JSONArray ret = new JSONArray();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(subState).openSession()) {
            List params = new ArrayList<>();
            StringBuilder searchProdCmd = new StringBuilder();
            searchProdCmd.append(
                    "SELECT PROD_CODE, PROD_NAME, BUSINESS_LINE, BUSINESS_DEPT, COMPANY FROM T_SHELF_PRODUCT WHERE UUID IN (SELECT DISTINCT PROD_UUID FROM T_SHELF_PRODUCT_VCS WHERE COMP_UUID ISNULL AND STATUS NOT IN(225)) ");   //change 400, 226 to 225 confirm by Megic 15/09/2020
            if (null != productCode && !productCode.isEmpty()) {
                searchProdCmd.append(" AND PROD_CODE = ? ");
                params.add(productCode);
            }
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(searchProdCmd.toString());
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
                JSONObject obj = new JSONObject().put("prodCode", ValidUtils.null2NoData(rs.getString("PROD_CODE")))
                        .put("prodName", ValidUtils.null2NoData(rs.getString("PROD_NAME")))
                        .put("businessLine", ValidUtils.null2NoData(rs.getString("BUSINESS_LINE")))
                        .put("businessDept", ValidUtils.null2NoData(rs.getString("BUSINESS_DEPT")))
                        .put("company", ValidUtils.null2NoData(rs.getString("COMPANY")));
                ret.put(obj);
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
        return ret;
    }

    public HashMap getProductByStatus(String dbEnv, String prodUuid, Integer status) throws SQLException {
        HashMap hmp = new HashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append(
                    "SELECT vcs.PROD_UUID AS PROD_UUID,DTL.uuid AS DTL_UUID, DTL.LK_CODE AS LK_CODE, DTL.LK_VALUE AS LK_VALUE, vcs.VER_PROD AS VER_PROD, vcs.STATE STATE  "
                    + " FROM T_SHELF_PRODUCT_VCS vcs , T_SHELF_PRODUCT_DTL DTL " + " WHERE 1=1 "
                    + " AND vcs.UUID = DTL.TRN_UUID" + " AND vcs.COMP_UUID IS NULL"
                    + " AND DTL.LK_CODE IN ('activeDate','endDate')" + " AND vcs.PROD_UUID = ? "
                    + " AND vcs.STATUS = ? ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(taskCategoryCmd.toString());
            ps.setString(1, prodUuid);
            ps.setInt(2, status);
            rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = ValidUtils.null2NoData(rs.getString("PROD_UUID"));
                String dtlUuid = ValidUtils.null2NoData(rs.getString("DTL_UUID"));
                String ver = ValidUtils.null2NoData(rs.getInt("VER_PROD"));
                String lkCode = ValidUtils.null2NoData(rs.getString("LK_CODE"));
                String lkValue = ValidUtils.null2NoData(rs.getString("LK_VALUE"));
                String state = ValidUtils.null2NoData(rs.getString("STATE"));
                JSONObject data = new JSONObject();
                data.put("uuid", uuid).put("dtlUuid", dtlUuid).put("ver", ver).put("activeDate", "").put("endDate", "")
                        .put("state", state);
                String kMap = uuid;// + "_" + ver;
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
        return hmp;
    }

    public JSONObject getProductByStatus(String dbEnv, Integer status, String prodUuid) throws SQLException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONObject ret = new JSONObject().put("uuid", prodUuid).put("activeDate", "").put("endDate", "");
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder taskCategoryCmd = new StringBuilder();
            taskCategoryCmd.append(
                    "SELECT vcs.PROD_UUID AS PROD_UUID,DTL.uuid AS DTL_UUID, DTL.LK_CODE AS LK_CODE, DTL.LK_VALUE AS LK_VALUE, vcs.VER_PROD AS VER_PROD, vcs.STATE STATE  "
                    + " FROM T_SHELF_PRODUCT_VCS vcs , T_SHELF_PRODUCT_DTL DTL " + " WHERE 1=1 "
                    + " AND vcs.UUID = DTL.TRN_UUID" + " AND vcs.COMP_UUID IS NULL"
                    + " AND DTL.LK_CODE IN ('activeDate','endDate')" + " AND vcs.STATUS = ? "
                    + " AND vcs.PROD_UUID = ? ");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(taskCategoryCmd.toString());
            ps.setInt(1, status);
            ps.setString(2, prodUuid);
            rs = ps.executeQuery();
            while (rs.next()) {
                String uuid = ValidUtils.null2NoData(rs.getString("PROD_UUID"));
                String ver = ValidUtils.null2NoData(rs.getInt("VER_PROD"));
                String lkCode = ValidUtils.null2NoData(rs.getString("LK_CODE"));
                String lkValue = ValidUtils.null2NoData(rs.getString("LK_VALUE"));
                ret.put("ver", ver);
                if ("activeDate".equalsIgnoreCase(lkCode)) {
                    ret.put("activeDate", lkValue);
                }
                if ("endDate".equalsIgnoreCase(lkCode)) {
                    ret.put("endDate", lkValue);
                }
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
        return ret;
    }

    public HashMap getProductComponentByVersion(String dbEnv, String prodUuid, Integer prodVer) {
        HashMap hmp = new HashMap();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder productVcsCmd = new StringBuilder();
            productVcsCmd
                    .append("select s2.comp_uuid,c1.comp_code,c1.comp_name,s2.ver_prod,s1.lk_code,s1.lk_value,s1.attr1 "
                            + " from public.t_shelf_product_dtl s1 "
                            + " inner join public.t_shelf_product_vcs s2 on s1.trn_uuid = s2.uuid "
                            + " left join public.t_shelf_comp c1 on c1.uuid = s2.comp_uuid " + " where 1=1 "
                            + " and s2.prod_uuid = ? " + " and s2.ver_prod = ? " + " order by c1.comp_code,s1.lk_code");
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(productVcsCmd.toString());
            ps.setString(1, prodUuid);
            ps.setInt(2, prodVer);
            rs = ps.executeQuery();
            while (rs.next()) {
                String compCode = ValidUtils.null2Separator(rs.getString("comp_code"), "INITIAL");
                String compName = ValidUtils.null2Separator(rs.getString("comp_name"), "INITIAL PRODUCT");
                String lkCode = ValidUtils.null2NoData(rs.getString("lk_code"));
                String lkValue = ValidUtils.null2NoData(rs.getString("lk_value"));
                String attr1 = ValidUtils.null2NoData(rs.getString("attr1"));
                String kMap = compCode;
                JSONObject tmp = new JSONObject();
                if (null != hmp.get(kMap)) {
                    tmp = (JSONObject) hmp.get(kMap);
                }
                JSONObject data = new JSONObject().put("lkCode", lkCode).put("lkValue", lkValue).put("attr1", attr1);
//                arr.put(data);
                hmp.put(kMap, tmp.put(lkCode, data));
            }
        } catch (SQLException | HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (NullPointerException | SQLException ex) {
                logger.info(ex.getMessage());
            }
        }
        return hmp;
    }

    public JSONArray updateShelfProduct(String dbEnv, ShelfProduct shelfProduct, String termsNCondition, Integer statusActive, Integer statusInactive) {
        Transaction trans = null;
        JSONArray ret = new JSONArray();
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.saveOrUpdate(shelfProduct);
            List<ShelfProductVcs> list = shelfProduct.getShelfProductVcsList();
            if (null != list) {
                for (ShelfProductVcs vcs : list) {
                    if (null == vcs.getProdUuid()) {
                        vcs.setProdUuid(shelfProduct);
                    }
                    List<ShelfProductDtl> list2 = vcs.getShelfProductDtlList();
                    session.saveOrUpdate(vcs);
                    if (null != list2) {
                        for (ShelfProductDtl dtl : list2) {
                            if (null == dtl.getTrnUuid()) {
                                dtl.setTrnUuid(vcs);
                            }
                            if (!termsNCondition.isEmpty() && "termsNCondition".equalsIgnoreCase(dtl.getLkCode()) && (statusActive.equals(vcs.getStatus()) || statusInactive.equals(vcs.getStatus()))) {
                                dtl.setLkValue(termsNCondition);
                            }
                            session.saveOrUpdate(dtl);
                        }
                    }
                }
            }
            ret.put(new JSONObject().put("uuid", shelfProduct.getUuid()));
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            // e.printStackTrace();
            ret = new JSONArray();
            throw e;
        }
        return ret;
    }
}
