/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.HibernateException;
//import org.hibernate.Criteria;
//import org.hibernate.HibernateException;
//import org.hibernate.Session;
//import org.hibernate.Transaction;
//import org.hibernate.criterion.Order;
//import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import th.co.d1.digitallending.dao.ShelfTmpDao;
//import th.co.d1.digitallending.dao.ShelfThemeDao;
//import th.co.d1.digitallending.dao.ShelfTmpDao;
import th.co.d1.digitallending.dao.ShelfTmpDetailDao;
import th.co.d1.digitallending.dao.ShelfTmpVcsDao;
import th.co.d1.digitallending.dao.SysLookupDao;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfTmp;
//import th.co.d1.digitallending.entity.ShelfProductVcs;
//import th.co.d1.digitallending.entity.ShelfTheme;
//import th.co.d1.digitallending.entity.ShelfTmp;
import th.co.d1.digitallending.entity.ShelfTmpDetail;
import th.co.d1.digitallending.entity.ShelfTmpVcs;
//import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;

/**
 *
 * @author Kritsana
 */
public class TemplateUtils {

    Logger logger = Logger.getLogger(TemplateUtils.class.getName());
//    private Session session;  //เพิ่มเข้ามาใหม่ 04/06/2020

    public JSONArray getTemplateList(String dbEnv) {
        JSONArray jsonArr = new JSONArray();
        try {
            List<ShelfTmpVcs> shelfTmpVcs = new ShelfTmpVcsDao().getList(dbEnv);
            for (ShelfTmpVcs ver : shelfTmpVcs) {
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(ver.getStatus()));
                JSONObject eachList = new JSONObject()
                        .put("id", ver.getTmpUuid().getUuid())
                        .put("vcsUuid", ver.getUuid())
                        .put("name", ver.getTmpUuid().getTmpName())
                        .put("description", ver.getTmpUuid().getDescription())
                        .put("version", (ver.getVersion() == 0 ? "" : String.valueOf(ver.getVersion())))
                        .put("effectiveDate", ver.getEffectiveDate())
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("updateDate", (ver.getUpdateAt() == null ? "" : ver.getUpdateAt()))
                        .put("updateBy", (ver.getUpdateBy() == null ? "" : ver.getUpdateBy()))
                        .put("createDate", ver.getCreateAt())
                        .put("createBy", ver.getCreateBy());
                jsonArr.put(eachList);
            }
        } catch (NullPointerException | HibernateException e) {
            throw e;
        }
        return jsonArr;
    }

    /* Update Or Create - end */
    public JSONArray getTemplateListByStatus(String dbEnv, int status) {
        JSONArray returnVal = new JSONArray();
        try {
//            int statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
//        List<ShelfTmp> shelfTmps = new ShelfTmpDao().getListByStatus(dbEnv, statusActive);
            List<ShelfTmpVcs> list = new ShelfTmpVcsDao().getListByStatus(dbEnv, status);
//        int verNumb = 0;
//        String tmpVcsUuid = "";
            for (ShelfTmpVcs vcs : list) {
                /*for (ShelfTmpVcs vcs : shelfTmp.getShelfTmpVcsList()) {
                if (vcs.getStatus() == statusActive) {
                    verNumb = vcs.getVersion();
                    tmpVcsUuid = vcs.getUuid();
                    break;
                }
            }*/
                List<ShelfTmpDetail> shelfTmpDetail = new ShelfTmpDetailDao().getTemplateDetailByTemplateVcsUuid(dbEnv, vcs.getUuid(), status);
//                JSONArray compList = new JSONArray();
                List<JSONObject> compList = new ArrayList<>();
                for (ShelfTmpDetail tmpDtl : shelfTmpDetail) {
                    if (tmpDtl.getFlagEnable()) {
                        JSONObject tmpObj = new JSONObject();
                        tmpObj.put("uuid", ValidUtils.null2NoData(tmpDtl.getCompUuid().getUuid()));
                        tmpObj.put("seqNo", ValidUtils.null2NoData(tmpDtl.getSeqNo()));
                        tmpObj.put("compCode", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompCode()));
                        tmpObj.put("compName", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompName()));
                        tmpObj.put("description", ValidUtils.null2NoData(tmpDtl.getCompUuid().getDescription()));
                        tmpObj.put("pattern", ValidUtils.null2NoData(tmpDtl.getCompUuid().getPattern()));
                        tmpObj.put("value", ValidUtils.null2NoData(tmpDtl.getValue()));
//                    compList.put(tmpObj);
                        compList.add(tmpObj);
                    }
                }
                Utils.sortJSONObjectByKey(compList, "seqNo", true);
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(vcs.getStatus()));
                JSONObject eachList = new JSONObject()
                        .put("id", vcs.getTmpUuid().getUuid())
                        .put("vcsUuid", vcs.getUuid())
                        .put("name", vcs.getTmpUuid().getTmpName())
                        .put("value", vcs.getTmpUuid().getValue())
                        .put("currentVcsUuid", (vcs.getTmpUuid().getCurrentVcsUuid()))
                        .put("previousVcsUuid", vcs.getTmpUuid().getPreviousVcsUuid())
                        .put("description", vcs.getDescription())
                        .put("attr1", vcs.getAttr1())
                        .put("attr2", vcs.getAttr2())
                        .put("attr3", vcs.getAttr3())
                        .put("attr4", vcs.getAttr4())
                        .put("attr5", vcs.getAttr5())
                        .put("attr6", vcs.getAttr6())
                        .put("attr7", vcs.getAttr7())
                        .put("attr8", vcs.getAttr8())
                        .put("attr9", vcs.getAttr9())
                        .put("attr10", vcs.getAttr10())
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("version", vcs.getVersion())
                        .put("companyCode", vcs.getTmpUuid().getCompanyCode())
                        .put("businessDept", vcs.getTmpUuid().getBussinessDept())
                        .put("businessLine", vcs.getTmpUuid().getBusinessLine())
                        .put("updateDate", (vcs.getUpdateAt() == null ? "" : vcs.getUpdateAt()))
                        .put("updateBy", (vcs.getUpdateBy() == null ? "" : vcs.getUpdateBy()))
                        .put("createDate", vcs.getCreateAt())
                        .put("createBy", vcs.getCreateBy())
                        .put("effectiveDate", vcs.getEffectiveDate())
                        .put("component", compList);
                returnVal.put(eachList);
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return returnVal;
    }

    public JSONArray getActiveInActiveTemplateList(String dbEnv) {
        JSONArray returnVal = new JSONArray();
        try {
            List<ShelfTmpVcs> list = new ShelfTmpVcsDao().getActiveInActiveList(dbEnv);
            for (ShelfTmpVcs vcs : list) {
                List<ShelfTmpDetail> shelfTmpDetail = new ShelfTmpDetailDao().getActiveInActiveByVcsUuid(dbEnv, vcs.getUuid());
                List<JSONObject> compList = new ArrayList<>();
                for (ShelfTmpDetail tmpDtl : shelfTmpDetail) {
                    if (tmpDtl.getFlagEnable()) {
                        JSONObject tmpObj = new JSONObject();
                        tmpObj.put("uuid", ValidUtils.null2NoData(tmpDtl.getCompUuid().getUuid()));
                        tmpObj.put("seqNo", ValidUtils.null2NoData(tmpDtl.getSeqNo()));
                        tmpObj.put("compCode", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompCode()));
                        tmpObj.put("compName", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompName()));
                        tmpObj.put("description", ValidUtils.null2NoData(tmpDtl.getCompUuid().getDescription()));
                        tmpObj.put("pattern", ValidUtils.null2NoData(tmpDtl.getCompUuid().getPattern()));
                        tmpObj.put("value", ValidUtils.null2NoData(tmpDtl.getValue()));
                        compList.add(tmpObj);
                    }
                }
                Utils.sortJSONObjectByKey(compList, "seqNo", true);
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(vcs.getStatus()));
                JSONObject eachList = new JSONObject()
                        .put("id", vcs.getTmpUuid().getUuid())
                        .put("vcsUuid", vcs.getUuid())
                        .put("name", vcs.getTmpUuid().getTmpName())
                        .put("value", vcs.getTmpUuid().getValue())
                        .put("currentVcsUuid", (vcs.getTmpUuid().getCurrentVcsUuid()))
                        .put("previousVcsUuid", vcs.getTmpUuid().getPreviousVcsUuid())
                        .put("description", vcs.getDescription())
                        .put("attr1", vcs.getAttr1())
                        .put("attr2", vcs.getAttr2())
                        .put("attr3", vcs.getAttr3())
                        .put("attr4", vcs.getAttr4())
                        .put("attr5", vcs.getAttr5())
                        .put("attr6", vcs.getAttr6())
                        .put("attr7", vcs.getAttr7())
                        .put("attr8", vcs.getAttr8())
                        .put("attr9", vcs.getAttr9())
                        .put("attr10", vcs.getAttr10())
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("version", vcs.getVersion())
                        .put("companyCode", vcs.getTmpUuid().getCompanyCode())
                        .put("businessDept", vcs.getTmpUuid().getBussinessDept())
                        .put("businessLine", vcs.getTmpUuid().getBusinessLine())
                        .put("updateDate", (vcs.getUpdateAt() == null ? "" : vcs.getUpdateAt()))
                        .put("updateBy", (vcs.getUpdateBy() == null ? "" : vcs.getUpdateBy()))
                        .put("createDate", vcs.getCreateAt())
                        .put("createBy", vcs.getCreateBy())
                        .put("effectiveDate", vcs.getEffectiveDate())
                        .put("component", compList);
                returnVal.put(eachList);
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return returnVal;
    }

    public JSONArray getTemplateListByStatus(String dbEnv, List status) {
        JSONArray returnVal = new JSONArray();
        try {
            List<ShelfTmpVcs> list = new ShelfTmpVcsDao().getListByStatus(dbEnv, status);
            for (ShelfTmpVcs vcs : list) {
                List<JSONObject> compList = new ArrayList<>();
                for (ShelfTmpDetail tmpDtl : vcs.getShelfTmpDetailList()) {
                    JSONObject tmpObj = new JSONObject();
                    tmpObj.put("uuid", ValidUtils.null2NoData(tmpDtl.getCompUuid().getUuid()));
                    tmpObj.put("seqNo", ValidUtils.null2NoData(tmpDtl.getSeqNo()));
                    tmpObj.put("compCode", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompCode()));
                    tmpObj.put("compName", ValidUtils.null2NoData(tmpDtl.getCompUuid().getCompName()));
                    tmpObj.put("description", ValidUtils.null2NoData(tmpDtl.getCompUuid().getDescription()));
                    tmpObj.put("pattern", ValidUtils.null2NoData(tmpDtl.getCompUuid().getPattern()));
                    tmpObj.put("value", ValidUtils.null2NoData(tmpDtl.getValue()));
//                    compList.put(tmpObj);
                    compList.add(tmpObj);
                }
                Utils.sortJSONObjectByKey(compList, "seqNo", true);
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(vcs.getStatus()));
                JSONObject eachList = new JSONObject()
                        .put("id", vcs.getTmpUuid().getUuid())
                        .put("vcsUuid", vcs.getUuid())
                        .put("name", vcs.getTmpUuid().getTmpName())
                        .put("value", vcs.getTmpUuid().getValue())
                        .put("currentVcsUuid", (vcs.getTmpUuid().getCurrentVcsUuid()))
                        .put("previousVcsUuid", vcs.getTmpUuid().getPreviousVcsUuid())
                        .put("description", vcs.getDescription())
                        .put("attr1", vcs.getAttr1())
                        .put("attr2", vcs.getAttr2())
                        .put("attr3", vcs.getAttr3())
                        .put("attr4", vcs.getAttr4())
                        .put("attr5", vcs.getAttr5())
                        .put("attr6", vcs.getAttr6())
                        .put("attr7", vcs.getAttr7())
                        .put("attr8", vcs.getAttr8())
                        .put("attr9", vcs.getAttr9())
                        .put("attr10", vcs.getAttr10())
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("version", vcs.getVersion())
                        .put("companyCode", vcs.getTmpUuid().getCompanyCode())
                        .put("businessDept", vcs.getTmpUuid().getBussinessDept())
                        .put("businessLine", vcs.getTmpUuid().getBusinessLine())
                        .put("updateDate", (vcs.getUpdateAt() == null ? "" : vcs.getUpdateAt()))
                        .put("updateBy", (vcs.getUpdateBy() == null ? "" : vcs.getUpdateBy()))
                        .put("createDate", vcs.getCreateAt())
                        .put("createBy", vcs.getCreateBy())
                        .put("effectiveDate", vcs.getEffectiveDate())
                        .put("component", compList);
                returnVal.put(eachList);
            }
        } catch (JSONException | NullPointerException | HibernateException e) {
            logger.info(e.getMessage());
            throw e;
        }
        return returnVal;
    }
//    public JSONArray getTemplateList(String dbEnv ,boolean onlyActive) {
//        JSONArray jsonArr = new JSONArray();
////        List<ShelfTmp> list = new ArrayList<>();
//        List<ShelfTmpVcs> shelfTmpVcs = new ShelfTmpVcsDao().getList(dbEnv);
//        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
//        Transaction trans;
//        try {
//            int statusActive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
//            int statusInactive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inactive").getLookupcode());
//            int statusCancel = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "cancel").getLookupcode());
//            int statusDelete = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "delete").getLookupcode());
//            List statusActiveInactive = new ArrayList();
//            statusActiveInactive.add(statusActive);
//            statusActiveInactive.add(statusInactive);
//            List statusNotIn = new ArrayList();
//            statusNotIn.add(statusCancel);
//            statusNotIn.add(statusDelete);
//            session = getSessionMaster(dbEnv).openSession();
//            trans = session.beginTransaction();
//            Criteria criteria = session.createCriteria(ShelfTmpVcs.class);
//            if (onlyActive) {
//                criteria.add(Restrictions.eq("status", statusActive));
//            } else {
//                criteria.add(Restrictions.in("status", statusActiveInactive));
//            }
//            criteria.addOrder(Order.asc("themeName"));
//            shelfTmpVcs = criteria.list();
//        for (ShelfTmpVcs ver : shelfTmpVcs) {
//            Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(ver.getStatus()));
//            JSONObject eachList = new JSONObject()
//                    .put("id", ver.getTmpUuid().getUuid())
//                    .put("vcsUuId", ver.getUuid())
//                    .put("name", ver.getTmpUuid().getTmpName())
//                    .put("description", ver.getTmpUuid().getDescription())
//                    .put("version", (ver.getVersion() == 0 ? "" : String.valueOf(ver.getVersion())))
//                    .put("effectiveDate", ver.getEffectiveDate())
//                    .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
//                    .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
//                    .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
//                    .put("updateDate", (ver.getUpdateAt() == null ? "" : ver.getUpdateAt()))
//                    .put("updateBy", (ver.getUpdateBy() == null ? "" : ver.getUpdateBy()))
//                    .put("createDate", ver.getCreateAt())
//                    .put("createBy", ver.getCreateBy());
//            criteria = session.createCriteria(ShelfProductVcs.class);
//                criteria.createAlias("themeUuid", "theme");
//                criteria.add(Restrictions.eq("theme.uuid", ver.getUuid()));
//                criteria.add(Restrictions.isNull("compUuid"));
//                criteria.add(Restrictions.not(Restrictions.in("status", statusNotIn)));
//                shelfProductVcsList = criteria.list();
//                JSONArray prodArr = new JSONArray();
//                for (ShelfProductVcs prodVcs : shelfProductVcsList) {
//                    JSONObject prodObj = new JSONObject();
//                    memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(prodVcs.getStatus()));
//                    prodObj.put("code", prodVcs.getProdUuid().getProdCode())
//                            .put("name", prodVcs.getProdUuid().getProdName())
//                            .put("verProd", prodVcs.getVerProd())
//                            .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
//                            .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
//                            .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
//                    prodArr.put(prodObj);
//                }
//                eachList.put("prodUsage", prodArr);
//            jsonArr.put(eachList);
//            }
//            trans.commit();
//            session.close();
//        } catch (HibernateException | NullPointerException e) {
//            logger.info(e.getMessage());
//        } finally {
//            if (null != session) {
//                session.close();
//            }
//        }
//        
//        return jsonArr;
//    }
////เพิ่มเข้ามาใหม่ 04/06/2020
//    public boolean getTemplateList(String validateSubStateFromHeader) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    public static void setActiveExpireTemplate(String dbEnv) throws SQLException {
//        JSONObject result = new JSONObject().put("status", 200).put("description", "");
        try {
            Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
            ShelfTmpDao dao = new ShelfTmpDao();
            List<ShelfTmp> listTmp = dao.getShelfTemplateByTemplateVcsStatus(dbEnv, statusInactive);
            Date expireDate = new Date();
            listTmp.forEach((tmp) -> {
                ShelfTmpVcs active = null, expired = null;
                for (ShelfTmpVcs vcs : tmp.getShelfTmpVcsList()) {
                    if (vcs.getStatus() == statusInactive) {
                        if (vcs.getEffectiveDate().compareTo(expireDate) <= 0) {
                            active = vcs;
                        }
                    } else if (vcs.getStatus() == statusActive) {
                        expired = vcs;
                    }
                }
                try {
                    if (active != null && expired != null) {
                        new ShelfTmpVcsDao().updateStatus(dbEnv, expired.getUuid(), statusExpire, StatusUtils.setStatus(expired.getState(), ValidUtils.null2NoData(statusExpire)), "", false);
                        new ShelfTmpVcsDao().updateStatus(dbEnv, active.getUuid(), statusActive, StatusUtils.setStatus(active.getState(), ValidUtils.null2NoData(statusActive)), "", false);
                    } else if (active != null && expired == null) {
                        new ShelfTmpVcsDao().updateStatus(dbEnv, active.getUuid(), statusActive, StatusUtils.setStatus(active.getState(), ValidUtils.null2NoData(statusActive)), "", false);
                    } else if (active == null && expired != null) {
//                    new ShelfTmpVcsDao().updateStatus(dbEnv, expired.getUuid(), statusExpire, StatusUtils.setStatus(expired.getState(), ValidUtils.null2NoData(statusExpire)), "", false);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(TemplateUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (HibernateException | NullPointerException e) {
            throw e;
//            logger.info(e.getMessage());
//            result.put("status", 500).put("description", "" + e);
        }
//        return result;
    }
}
