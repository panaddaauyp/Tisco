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
import org.apache.log4j.Logger;
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
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
import th.co.d1.digitallending.util.DateUtils;

import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Poomsakul Senakul
 */
public class ShelfTmpVcsDao {

    private Session session;
    Logger logger = Logger.getLogger(ShelfTmpVcsDao.class);

    public List<ShelfTmpVcs> getList(String dbEnv) {
        List<ShelfTmpVcs> shelfTmpVcs = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            criteria.addOrder(Order.asc("tmp.uuid"));
            criteria.addOrder(Order.desc("tmp.createAt"));
            criteria.addOrder(Order.asc("version"));
//            criteria.addOrder(Order.asc("seqNo"));
            shelfTmpVcs = criteria.list();
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
        return shelfTmpVcs;
    }

    public ShelfTmpVcs getListByUuid(String dbEnv, String vcsUuid) {
        ShelfTmpVcs shelfTmpVcs = new ShelfTmpVcs();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.add(Restrictions.eq("uuid", vcsUuid));
            shelfTmpVcs = (ShelfTmpVcs) criteria.uniqueResult();
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
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
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
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
            return new JSONObject().put("status", false).put("description", "" + e);
        }
    }

    public int maxVersion(String dbEnv, String tmpUuid) {
        int maxVersion = 0;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder prodCmd = new StringBuilder();
            prodCmd.append("select max(version) version from ShelfTmpVcs where tmpUuid.uuid = ? ");
//            System.out.println("query : " + SQL_QUERY);
            ps = con.prepareStatement(prodCmd.toString());
            ps.setString(1, tmpUuid);
            rs = ps.executeQuery();
            List list = new ArrayList<>();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            if (list.get(0) == null) {
                maxVersion = 0;
            }
            rs.close();
            ps.close();
            session.close();
        } catch (HibernateException | NullPointerException | SQLException e) {
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
        return maxVersion;
    }

    public JSONObject updateStatus(String dbEnv, String vcsUuid, int status, String state, String remark, boolean upVersion) {
        Transaction trans = null;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            con = session.doReturningWork((Connection conn) -> conn);
            StringBuilder prodCmd = new StringBuilder();
            if (remark.isEmpty()) {
                prodCmd.append("update t_shelf_tmp_vcs set status = ?, state = ? Where uuid = ?");
                ps = con.prepareStatement(prodCmd.toString());
                ps.setInt(1, status);
                ps.setString(2, state);
                ps.setString(3, vcsUuid);
            } else {
                prodCmd.append("update t_shelf_tmp_vcs set status = ?, state = ?, attr2 = ? Where uuid = ?");
                ps = con.prepareStatement(prodCmd.toString());
                ps.setInt(1, status);
                ps.setString(2, state);
                ps.setString(3, remark);
                ps.setString(4, vcsUuid);
            }
            ps.executeUpdate();
            if (upVersion) {
                prodCmd.setLength(0);
                prodCmd.append("UPDATE t_shelf_tmp_vcs SET version = (SELECT MAX(version)+1 FROM t_shelf_tmp_vcs where tmp_uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)) where tmp_uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)");
                ps = con.prepareStatement(prodCmd.toString());
                ps.setString(1, vcsUuid);
                ps.setString(2, vcsUuid);
                ps.executeUpdate();
                prodCmd.setLength(0);
                prodCmd.append("UPDATE t_shelf_tmp SET current_vcs_uuid = ? where uuid = (select tmp_uuid from t_shelf_tmp_vcs where uuid = ?)");
                ps = con.prepareStatement(prodCmd.toString());
                ps.setString(1, vcsUuid);
                ps.setString(2, vcsUuid);
                ps.executeUpdate();
            }
            trans.commit();

            session.close();
            return new JSONObject().put("status", true).put("description", "");
        } catch (NullPointerException | HibernateException | SQLException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.error("" + e);
            e.printStackTrace();
            return new JSONObject().put("status", false).put("description", "" + e);
        } finally {
            if (null != session) {
                session.close();
            }
//                if (!con.isClosed()) {
//                    con.close();
//                }
        }
    }

    public List<ShelfTmpVcs> getListByStatus(String dbEnv, int status) {
        List<ShelfTmpVcs> shelfTmp = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.add(Restrictions.eq("status", status));
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

    public List<ShelfTmpVcs> getShelfTmpVcsByTmpUUID(String dbEnv, String tmpUuid, String tmpVcsUuid) {
        List<ShelfTmpVcs> listVcs = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpVcs> cr = cb.createQuery(ShelfTmpVcs.class);
            Root<ShelfTmpVcs> root = cr.from(ShelfTmpVcs.class);
            Join<ShelfTmpVcs, ShelfTmp> joinObject = root.join("tmpUuid");
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), tmpUuid), cb.notEqual(root.get("uuid"), tmpVcsUuid)));
            Query<ShelfTmpVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.list();
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
        return listVcs;
    }

    public List<ShelfTmpVcs> getListByTmpUuidAndStatus(String dbEnv, String tmpUuid, int status) {
        List<ShelfTmpVcs> listVcs = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfTmpVcs> cr = cb.createQuery(ShelfTmpVcs.class);
            Root<ShelfTmpVcs> root = cr.from(ShelfTmpVcs.class);
            Join<ShelfTmpVcs, ShelfTmp> joinObject = root.join("tmpUuid");
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), tmpUuid), cb.notEqual(root.get("status"), status)));
            Query<ShelfTmpVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.list();
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
        return listVcs;
    }

    public JSONArray searchShelfTemplate(String dbEnv, String templateName, Integer status, Date createFrom, Date createTo, Date updateFrom, Date updateTo, Date effectiveStartDate, Date effectiveEndDate) {
        List<ShelfTmpVcs> templates = new ArrayList<>();
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        JSONArray jsonArr = new JSONArray();
        Transaction trans;
        int statusActive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
        int statusInactive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inactive").getLookupcode());
        List statusActiveInactive = new ArrayList();
        statusActiveInactive.add(statusActive);
        statusActiveInactive.add(statusInactive);
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            if (null != templateName && !"".equals(templateName)) {
                criteria.add(Restrictions.like("tmp.tmpName", templateName, MatchMode.ANYWHERE));
            }
            if (null != status) {
                criteria.add(Restrictions.eq("status", status));
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
                        .put("tmpUuid", shelfTmpVcs.getTmpUuid().getUuid())
                        .put("vcsUuid", shelfTmpVcs.getUuid())
                        .put("name", shelfTmpVcs.getTmpUuid().getTmpName())
                        .put("effectiveDate", shelfTmpVcs.getEffectiveDate())
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("createDate", shelfTmpVcs.getCreateAt())
                        .put("createDate", shelfTmpVcs.getCreateBy())
                        .put("version", shelfTmpVcs.getVersion())
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
                JSONObject tmpObj = new JSONObject()
                        .put("header", header)
                        .put("info", infoData);
                criteria = session.createCriteria(ShelfProductVcs.class);
                criteria.add(Restrictions.eq("temUuid", shelfTmpVcs.getTmpUuid().getUuid()));
                criteria.add(Restrictions.isNull("compUuid"));
                criteria.add(Restrictions.in("status", statusActiveInactive));
                shelfProductVcsList = criteria.list();
                JSONArray prodArr = new JSONArray();
                for (ShelfProductVcs prodVcs : shelfProductVcsList) {
                    JSONObject prodObj = new JSONObject();
                    memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(prodVcs.getStatus()));
                    prodObj.put("code", prodVcs.getProdUuid().getProdCode())
                            .put("name", prodVcs.getProdUuid().getProdName())
                            .put("verProd", prodVcs.getVerProd())
                            .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                            .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                            .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                    prodArr.put(prodObj);
                }
                tmpObj.put("prodUsage", prodArr);
                jsonArr.put(tmpObj);
            }
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return jsonArr;
    }

    public List<ShelfTmpVcs> getShelfTemplateByNameStatusActiveAndInActive(String dbEnv, String templateName) {
        List<ShelfTmpVcs> list = new ArrayList<>();
        Transaction trans = null;
        try {
            int statusActive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            int statusInactive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inactive").getLookupcode());
            List status = new ArrayList();
            status.add(statusActive);
            status.add(statusInactive);
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
            criteria.createAlias("tmpUuid", "tmp");
            if (null != templateName && !"".equals(templateName)) {
                criteria.add(Restrictions.eq("tmp.tmpName", templateName));
            }
            criteria.add(Restrictions.in("status", status));
            criteria.addOrder(Order.asc("themeName"));
            list = criteria.list();
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
        return list;
    }
}
