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
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfTmpVcsDao {

    Logger logger = Logger.getLogger(ShelfTmpVcsDao.class.getName());

    public List<ShelfTmpVcs> getList(String dbEnv) {
        List<ShelfTmpVcs> shelfTmpVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            criteria.addOrder(Order.asc("tmp.uuid"));
            criteria.addOrder(Order.desc("tmp.createAt"));
            criteria.addOrder(Order.asc("version"));
//            criteria.addOrder(Order.asc("seqNo"));
            shelfTmpVcs = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpVcs;
    }

    public ShelfTmpVcs getListByUuid(String dbEnv, String vcsUuid) {
        ShelfTmpVcs shelfTmpVcs = new ShelfTmpVcs();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.add(Restrictions.eq("uuid", vcsUuid));
            shelfTmpVcs = (ShelfTmpVcs) criteria.uniqueResult();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpVcs;
    }

    public List<ShelfTmpVcs> getListByTmpUuid(String dbEnv, String tmpUuid) {
        List<ShelfTmpVcs> shelfTmpVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            criteria.add(Restrictions.eq("tmp.uuid", tmpUuid));
//            criteria.add(Restrictions.not(Restrictions.eq("version", 0)));
            criteria.addOrder(Order.asc("version"));
//            criteria.addOrder(Order.asc("seqNo"));
            shelfTmpVcs = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmpVcs;
    }

    public JSONObject saveVcs(Session session, ShelfTmpVcs shelfTmpVcs) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.save(shelfTmpVcs);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            //e.printStackTrace();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateVcs(Session session, ShelfTmpVcs shelfTmpVcs) {
        Transaction trans = null;
        try {
            trans = session.beginTransaction();
            session.update(shelfTmpVcs);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public JSONObject updateVcs(String dbEnv, ShelfTmpVcs shelfTmpVcs) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.update(shelfTmpVcs);
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (HibernateException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }
    
    public int maxVersion(String dbEnv, String tmpUuid) throws SQLException {
        int maxVersion = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("select max(version) version from ShelfTmpVcs where tmpUuid.uuid = ? ");
//            System.out.println("query : " + SQL_QUERY);
            ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
            ps.setString(1, tmpUuid);
            rs = ps.executeQuery();
            List list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            if (list.get(0) == null) {
                maxVersion = 0;
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
        return maxVersion;
    }

    public JSONObject updateStatus(String dbEnv, String vcsUuid, int status, String state, String remark, boolean upVersion) throws SQLException {
        Transaction trans = null;
        PreparedStatement ps = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            StringBuilder prodCmd = new StringBuilder();
            if (remark == null || remark.isEmpty()) {
                prodCmd.append("update t_shelf_tmp_vcs set status = ?, state = ? Where uuid = ?");
                ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
                ps.setInt(1, status);
                ps.setString(2, state);
                ps.setString(3, vcsUuid);
            } else {
                prodCmd.append("update t_shelf_tmp_vcs set status = ?, state = ?, attr2 = ? Where uuid = ?");
                ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
                ps.setInt(1, status);
                ps.setString(2, state);
                ps.setString(3, remark);
                ps.setString(4, vcsUuid);
            }
            ps.executeUpdate();
            if (ps != null) {
                ps.close();
            }
            if (upVersion) {
                prodCmd.setLength(0);
//                prodCmd.append("UPDATE t_shelf_tmp_vcs SET version = (SELECT MAX(version)+1 FROM t_shelf_tmp_vcs where tmp_uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)) where tmp_uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)");
                prodCmd.append("UPDATE t_shelf_tmp_vcs SET version = (SELECT MAX(version)+1 FROM t_shelf_tmp_vcs where tmp_uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)) where uuid = ? ");
                ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
                ps.setString(1, vcsUuid);
                ps.setString(2, vcsUuid);
                ps.executeUpdate();
                if (!ps.isClosed()) {
                    ps.close();
                }
                prodCmd.setLength(0);
                prodCmd.append("UPDATE t_shelf_tmp SET current_vcs_uuid = ? where uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)");
                ps = session.doReturningWork((Connection conn) -> conn).prepareStatement(prodCmd.toString());
                ps.setString(1, vcsUuid);
                ps.setString(2, vcsUuid);
                ps.executeUpdate();
                if (!ps.isClosed()) {
                    ps.close();
                }
            }
            trans.commit();
            return new JSONObject().put("status", true).put("description", "");
        } catch (NullPointerException | HibernateException e) {
            if (!ps.isClosed()) {
                ps.close();
            }
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            //e.printStackTrace();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public List<ShelfTmpVcs> getListByStatus(String dbEnv, int status) {
        List<ShelfTmpVcs> shelfTmp = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.add(Restrictions.eq("status", status));
            shelfTmp = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmp;
    }

    public List<ShelfTmpVcs> getActiveInActiveList(String dbEnv) {
        List<ShelfTmpVcs> shelfTmp = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            shelfTmp = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmp;
    }

    public List<ShelfTmpVcs> getShelfTmpVcsByTmpUUID(String dbEnv, String tmpUuid, String tmpVcsUuid) {
        List<ShelfTmpVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpVcs> cr = cb.createQuery(ShelfTmpVcs.class);
            Root<ShelfTmpVcs> root = cr.from(ShelfTmpVcs.class);
            Join<ShelfTmpVcs, ShelfTmp> joinObject = root.join("tmpUuid");
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), tmpUuid), cb.notEqual(root.get("uuid"), tmpVcsUuid)));
            Query<ShelfTmpVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }

    public List<ShelfTmpVcs> getListByTmpUuidAndStatus(String dbEnv, String tmpUuid, int status, String tmpVcsUuid) {
        List<ShelfTmpVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpVcs> cr = cb.createQuery(ShelfTmpVcs.class);
            Root<ShelfTmpVcs> root = cr.from(ShelfTmpVcs.class);
            Join<ShelfTmpVcs, ShelfTmp> joinObject = root.join("tmpUuid");
            cr.select(root).where(cb.and(cb.and(cb.equal(joinObject.get("uuid"), tmpUuid), cb.notEqual(root.get("uuid"), tmpVcsUuid)), cb.equal(root.get("status"), status)));
            Query<ShelfTmpVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }

    public JSONArray searchShelfTemplate(String dbEnv, String templateName, JSONArray status, Date createFrom, Date createTo, Date updateFrom, Date updateTo, Date effectiveStartDate, Date effectiveEndDate) throws SQLException {
        List<ShelfTmpVcs> templates = new ArrayList<>();
        JSONArray jsonArr = new JSONArray();
        Transaction trans = null;
        Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
        Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
        Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
        Integer statusPause = StatusUtils.getPause(dbEnv).getStatusCode();
        Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
        Integer statusWaitToDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
        Integer statusTerminate = StatusUtils.getTerminate(dbEnv).getStatusCode();
        Integer statusDelete = StatusUtils.getDelete(dbEnv).getStatusCode();
        Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
        List statusProductIn = new ArrayList();
        statusProductIn.add(statusActive);
        statusProductIn.add(statusInactive);
        statusProductIn.add(statusInprogress);
        statusProductIn.add(statusPause);
        statusProductIn.add(statusWaitToApprove);
        statusProductIn.add(statusWaitToDelete);
        PreparedStatement ps = null;
        ResultSet rs = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            if (null != templateName && !"".equals(templateName)) {
                criteria.add(Restrictions.like("tmp.tmpName", templateName, MatchMode.ANYWHERE));
            }
            if (status.length() > 0) {
                List statusList = new ArrayList();
                for (int i = 0; i < status.length(); i++) {
                    statusList.add(status.getInt(i));
                }
                criteria.add(Restrictions.in("status", statusList));
            }
            if (null != createFrom && null != createTo) {
                criteria.add(Restrictions.between("createAt", DateUtils.utilDateToSqlDate(createFrom), DateUtils.utilDateToSqlDate(DateUtils.addDate(createTo, 0, 0, 0, 23, 59, 59))));
            } else if (null == createFrom && null != createTo) {
                criteria.add(Restrictions.lt("createAt", DateUtils.utilDateToSqlDate(DateUtils.addDate(createTo, 0, 0, 0, 23, 59, 59))));
            } else if (null != createFrom && null == createTo) {
                criteria.add(Restrictions.gt("createAt", DateUtils.utilDateToSqlDate(createFrom)));
            }
            if (null != updateFrom && null != updateTo) {
                criteria.add(Restrictions.between("createAt", DateUtils.utilDateToSqlDate(updateFrom), DateUtils.utilDateToSqlDate(DateUtils.addDate(updateTo, 0, 0, 0, 23, 59, 59))));
            } else if (null == updateFrom && null != updateTo) {
                criteria.add(Restrictions.lt("createAt", DateUtils.utilDateToSqlDate(DateUtils.addDate(updateTo, 0, 0, 0, 23, 59, 59))));
            } else if (null != updateFrom && null == updateTo) {
                criteria.add(Restrictions.gt("createAt", DateUtils.utilDateToSqlDate(updateFrom)));
            }
            if (null != effectiveStartDate && null != updateTo) {
                criteria.add(Restrictions.between("effectiveDate", DateUtils.utilDateToSqlDate(effectiveStartDate), DateUtils.utilDateToSqlDate(DateUtils.addDate(effectiveEndDate, 0, 0, 0, 23, 59, 59))));
            } else if (null == effectiveStartDate && null != effectiveEndDate) {
                criteria.add(Restrictions.lt("effectiveDate", DateUtils.utilDateToSqlDate(DateUtils.addDate(effectiveEndDate, 0, 0, 0, 23, 59, 59))));
            } else if (null != effectiveStartDate && null == effectiveEndDate) {
                criteria.add(Restrictions.gt("effectiveDate", DateUtils.utilDateToSqlDate(effectiveStartDate)));
            }
            templates = criteria.list();
            for (ShelfTmpVcs shelfTmpVcs : templates) {
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(shelfTmpVcs.getStatus()));
                JSONObject header = new JSONObject()
                        .put("id", shelfTmpVcs.getTmpUuid().getUuid())
                        .put("tmpUuid", shelfTmpVcs.getTmpUuid().getUuid())
                        .put("vcsUuid", shelfTmpVcs.getUuid())
                        .put("name", shelfTmpVcs.getTmpUuid().getTmpName())
                        .put("effectiveDate", shelfTmpVcs.getEffectiveDate())
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("createDate", shelfTmpVcs.getCreateAt())
                        .put("createBy", shelfTmpVcs.getCreateBy())
                        .put("version", (shelfTmpVcs.getVersion() == 0 ? "" : String.valueOf(shelfTmpVcs.getVersion())))
                        .put("updateDate", (shelfTmpVcs.getUpdateAt() == null ? "" : shelfTmpVcs.getUpdateAt()))
                        .put("updateBy", (shelfTmpVcs.getUpdateBy() == null ? "" : shelfTmpVcs.getUpdateBy()));
                JSONArray infoData = new JSONArray();
                for (int i = 0; i < shelfTmpVcs.getShelfTmpDetailList().size(); i++) {
                    ShelfTmpDetail detail = shelfTmpVcs.getShelfTmpDetailList().get(i);
                    JSONObject eachDetail = new JSONObject().put("seq", detail.getSeqNo())
                            .put("compUuid", detail.getCompUuid().getUuid())
                            .put("detailUuid", detail.getUuid())
                            .put("compCode", detail.getCompUuid().getCompCode())
                            .put("enable", detail.getFlagEnable());
                    if (detail.getCompUuid().getCompCode().equalsIgnoreCase("004")) {
                        if (shelfTmpVcs.getAttr1() != null && !shelfTmpVcs.getAttr1().isEmpty()) {
                            JSONArray attrArr = new JSONArray(shelfTmpVcs.getAttr1());
                            for (int aai = 0; aai < attrArr.length(); aai++) {
                                JSONObject obj = attrArr.getJSONObject(aai);
                                if (obj.has("termsNCondition")) {
                                    eachDetail.put("data", obj);
                                }
                            }
                        }
                    } else if (detail.getCompUuid().getCompCode().equalsIgnoreCase("006")) {
                        if (shelfTmpVcs.getAttr1() != null && !shelfTmpVcs.getAttr1().isEmpty()) {
                            JSONArray attrArr = new JSONArray(shelfTmpVcs.getAttr1());
                            for (int aai = 0; aai < attrArr.length(); aai++) {
                                JSONObject obj = attrArr.getJSONObject(aai);
                                if (obj.has("chkConsentList")) {
                                    eachDetail.put("data", obj);
                                }
                            }
                        }
                    } else {
                        eachDetail.put("data", detail.getValue() == null ? new JSONObject() : new JSONObject(detail.getValue()));
                    }
                    infoData.put(eachDetail);
                }
//                JSONObject tmpObj = new JSONObject()
//                        .put("header", header)
//                        .put("info", infoData);
                StringBuilder searchProdCmd = new StringBuilder();
                searchProdCmd.append("SELECT PROD_CODE, PROD_NAME, STATUS FROM T_SHELF_PRODUCT WHERE UUID IN (SELECT DISTINCT PROD_UUID FROM T_SHELF_PRODUCT_VCS WHERE COMP_UUID ISNULL ");
                searchProdCmd.append(" AND STATUS IN (? ");
                List params = new ArrayList<>();
                for (int i = 1; i < statusProductIn.size(); i++) {
                    searchProdCmd.append(", ? ");
                    params.add(statusProductIn.get(i - 1));
                }
                params.add(statusProductIn.get(statusProductIn.size() - 1));
                searchProdCmd.append(") ");
                searchProdCmd.append(" AND TEM_UUID = ?)");
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
                ps.setString(statusProductIn.size() + 1, shelfTmpVcs.getTmpUuid().getUuid());
                rs = ps.executeQuery();
                JSONArray prodArr = new JSONArray();
                while (rs.next()) {
                    if (rs.getInt("STATUS") == statusTerminate || rs.getInt("STATUS") == statusDelete || rs.getInt("STATUS") == statusExpire) {
                        prodArr = new JSONArray();
                        break;
                    }
                    JSONObject prodObj = new JSONObject();
                    memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(ValidUtils.null2NoData(rs.getInt("STATUS"))));
                    prodObj.put("code", ValidUtils.null2NoData(rs.getString("PROD_CODE")))
                            .put("name", ValidUtils.null2NoData(rs.getString("PROD_NAME")))
                            //                            .put("verProd", prodVcs.getVerProd())
                            .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                            .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                            .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                    prodArr.put(prodObj);
                }
                header.put("info", infoData);
                header.put("prodUsage", prodArr);
                jsonArr.put(header);
            }
        } catch (HibernateException | NullPointerException e) {
            try {
                if (rs != null && !rs.isClosed()) {
                    rs.close();
                }
                if (ps != null && !ps.isClosed()) {
                    ps.close();
                }
            } catch (SQLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            logger.info(e.getMessage());
            throw e;
        } finally {
            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
            if (ps != null && !ps.isClosed()) {
                ps.close();
            }
        }
        return jsonArr;
    }

    public List<ShelfTmpVcs> getShelfTemplateByNameStatusActiveAndInActive(String dbEnv, String templateName) {
        List<ShelfTmpVcs> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            int statusCancel = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "cancel").getLookupcode());
            int statusDelete = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "delete").getLookupcode());
            int statusTerminate = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "terminate").getLookupcode());
            List status = new ArrayList();
            status.add(statusCancel);
            status.add(statusDelete);
            status.add(statusTerminate);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            if (null != templateName && !"".equals(templateName)) {
                criteria.add(Restrictions.eq("tmp.tmpName", templateName));
            }
            criteria.add(Restrictions.not(Restrictions.in("status", status)));
            criteria.addOrder(Order.asc("tmp.tmpName"));
            list = criteria.list();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return list;
    }

    public List<ShelfTmpVcs> getListByStatus(String dbEnv, List status) {
        List<ShelfTmpVcs> shelfTmp = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.add(Restrictions.in("status", status));
            shelfTmp = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfTmp;
    }

    public List<ShelfTmpVcs> getListByTmpUuidAndTmpVersion(String dbEnv, String tmpUuid, int tmpVersion, boolean checkVerNotEq) {
        List<ShelfTmpVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpVcs> cr = cb.createQuery(ShelfTmpVcs.class);
            Root<ShelfTmpVcs> root = cr.from(ShelfTmpVcs.class);
            Join<ShelfTmpVcs, ShelfTmp> joinObject = root.join("tmpUuid");
            if (checkVerNotEq) {
                cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), tmpUuid), cb.notEqual(root.get("version"), tmpVersion)));
            } else {
                cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), tmpUuid), cb.equal(root.get("version"), tmpVersion)));
            }
            Query<ShelfTmpVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }
}
