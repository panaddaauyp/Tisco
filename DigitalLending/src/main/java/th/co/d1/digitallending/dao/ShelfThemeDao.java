/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.Memlookup;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTheme;
import th.co.d1.digitallending.util.DateUtils;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.ValidUtils;

/**
 *
 * @author Kritsana Sasai
 *
 * DataOne Asia (Thailand) Company Limited 900/29 Rama III Rd. Bangpongpang,
 * Yannawa, Bangkok 10120 Tel :+66 (0) 2682 3000
 *
 * @create 03-01-2020 10:12:03 AM
 */
public class ShelfThemeDao {

    Logger logger = Logger.getLogger(ShelfThemeDao.class.getName());

    public JSONArray getListShelfTmpTheme(String dbEnv, boolean onlyActive, boolean prodUsage) {
        List<ShelfTheme> list = new ArrayList<>();
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        JSONArray jsonArr = new JSONArray();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Integer statusActive = StatusUtils.getActive(dbEnv).getStatusCode();
            Integer statusInactive = StatusUtils.getInActive(dbEnv).getStatusCode();
            Integer statusInprogress = StatusUtils.getInprogress(dbEnv).getStatusCode();
            Integer statusPause = StatusUtils.getPause(dbEnv).getStatusCode();
            Integer statusWaitToApprove = StatusUtils.getWaittoApprove(dbEnv).getStatusCode();
            Integer statusWaitToDelete = StatusUtils.getWaittoDelete(dbEnv).getStatusCode();
            Integer statusTerminate = StatusUtils.getTerminate(dbEnv).getStatusCode();
            Integer statusDelete = StatusUtils.getDelete(dbEnv).getStatusCode();
            Integer statusExpire = StatusUtils.getExpired(dbEnv).getStatusCode();
            Integer statusCancel = StatusUtils.getCancel(dbEnv).getStatusCode();
            List statusProductIn = new ArrayList();
            statusProductIn.add(statusActive);
            statusProductIn.add(statusInactive);
            statusProductIn.add(statusInprogress);
            statusProductIn.add(statusPause);
            statusProductIn.add(statusWaitToApprove);
            statusProductIn.add(statusWaitToDelete);
            List statusActiveInactive = new ArrayList();
            statusActiveInactive.add(statusActive);
            statusActiveInactive.add(statusInactive);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTheme.class);
            if (onlyActive) {
                criteria.add(Restrictions.eq("status", statusActive));
            } else {
                criteria.add(Restrictions.in("status", statusActiveInactive));
            }
            criteria.addOrder(Order.asc("themeName"));
            list = criteria.list();
            for (ShelfTheme theme : list) {
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(theme.getStatus()));
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("uuid", theme.getUuid())
                        .put("code", ValidUtils.null2NoData(theme.getThemeCode()))
                        .put("name", ValidUtils.null2NoData(theme.getThemeName()))
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("createAt", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(theme.getCreateAt(), "yyyy-MM-dd HH:mm")))
                        .put("updateAt", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(theme.getUpdateAt(), "yyyy-MM-dd HH:mm"), DateUtils.getDisplayEnDate(theme.getCreateAt(), "yyyy-MM-dd HH:mm")))
                        .put("data", (theme.getValue() == null ? "" : theme.getValue()))
                        .put("createBy", ValidUtils.null2NoData(theme.getCreateBy()))
                        .put("updateBy", ValidUtils.null2NoData(theme.getUpdateBy()));
                if (prodUsage) {
                    criteria = session.createCriteria(ShelfProductVcs.class);
                    criteria.createAlias("themeUuid", "theme");
                    criteria.add(Restrictions.eq("theme.uuid", theme.getUuid()));
                    criteria.add(Restrictions.isNull("compUuid"));
                    criteria.add(Restrictions.in("status", statusProductIn));
                    shelfProductVcsList = criteria.list();
                    JSONArray prodArr = new JSONArray();
                    for (ShelfProductVcs prodVcs : shelfProductVcsList) {
                        if (prodVcs.getStatus() == statusTerminate || prodVcs.getStatus() == statusDelete || prodVcs.getStatus() == statusExpire) {
                            prodArr = new JSONArray();
                            break;
                        }
                        JSONObject prodObj = new JSONObject();
                        memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(prodVcs.getStatus()));
                        prodObj.put("code", prodVcs.getProdUuid().getProdCode())
                                .put("name", prodVcs.getProdUuid().getProdName())
                                .put("verProd", (prodVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(prodVcs.getVerProd())))
                                .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                                .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                                .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                        prodArr.put(prodObj);
                    }
                    jsonObj.put("prodUsage", prodArr);
                }
                jsonArr.put(jsonObj);
            }
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return jsonArr;
    }

    public JSONObject saveShelfTmpTheme(String dbEnv, ShelfTheme theme) {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            if (null == theme.getCreateAt()) {
                theme.setCreateAt(new Date());
            }
            session.save(theme);
            trans.commit();
            resp.put("status", true);
            resp.put("theme", theme);
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

    public ShelfTheme getShelfTmpTheme(String dbEnv, String uuid) {
        ShelfTheme theme = new ShelfTheme();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            theme = (ShelfTheme) session.get(ShelfTheme.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            //e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return theme;
    }

    public JSONObject updateShelfTmpTheme(String dbEnv, ShelfTheme theme) {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            if (null == theme.getUpdateAt()) {
                theme.setUpdateAt(new Date());
            }
            session.update(theme);
            trans.commit();
            resp.put("status", true);
            resp.put("theme", theme);
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

    public JSONArray searchShelfTheme(String dbEnv, String themeName, Integer status, Date createFrom, Date createTo, Date updateFrom, Date updateTo) {
        List<ShelfTheme> themes = new ArrayList<>();
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
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
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTheme.class);
            if (null != themeName && !"".equals(themeName)) {
                criteria.add(Restrictions.like("themeName", themeName, MatchMode.ANYWHERE));
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
            criteria.add(Restrictions.not(Restrictions.eq("status", 400)));
            themes = criteria.list();
            for (ShelfTheme theme : themes) {
                Memlookup memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(theme.getStatus()));
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("uuid", theme.getUuid())
                        .put("code", ValidUtils.null2NoData(theme.getThemeCode()))
                        .put("name", ValidUtils.null2NoData(theme.getThemeName()))
                        .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                        .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                        .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "")
                        .put("createAt", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(theme.getCreateAt(), "yyyy-MM-dd HH:mm")))
                        .put("updateAt", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(theme.getUpdateAt(), "yyyy-MM-dd HH:mm"), DateUtils.getDisplayEnDate(theme.getCreateAt(), "yyyy-MM-dd HH:mm")))
                        .put("data", (theme.getValue() == null ? "" : theme.getValue()))
                        .put("createBy", ValidUtils.null2NoData(theme.getCreateBy()))
                        .put("updateBy", ValidUtils.null2NoData(theme.getUpdateBy()));
                criteria = session.createCriteria(ShelfProductVcs.class);
                criteria.createAlias("themeUuid", "theme");
                criteria.add(Restrictions.eq("theme.uuid", theme.getUuid()));
                criteria.add(Restrictions.isNull("compUuid"));
                criteria.add(Restrictions.in("status", statusProductIn));
                shelfProductVcsList = criteria.list();
                JSONArray prodArr = new JSONArray();
                for (ShelfProductVcs prodVcs : shelfProductVcsList) {
                    if (prodVcs.getStatus() == statusTerminate || prodVcs.getStatus() == statusDelete || prodVcs.getStatus() == statusExpire) {
                        prodArr = new JSONArray();
                        break;
                    }
                    JSONObject prodObj = new JSONObject();
                    memLookup = new SysLookupDao().getMemLookupByCode(dbEnv, ValidUtils.null2NoData(prodVcs.getStatus()));
                    prodObj.put("code", prodVcs.getProdUuid().getProdCode())
                            .put("name", prodVcs.getProdUuid().getProdName())
                            .put("verProd", (prodVcs.getVerProd() == 0 ? "" : ValidUtils.obj2Int(prodVcs.getVerProd())))
                            .put("status", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupcode()) : "")
                            .put("statusNameTh", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameth()) : "")
                            .put("statusNameEn", null != memLookup ? ValidUtils.null2NoData(memLookup.getLookupnameen()) : "");
                    prodArr.put(prodObj);
                }
                jsonObj.put("prodUsage", prodArr);
                jsonArr.put(jsonObj);
            }
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (null != trans) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return jsonArr;
    }

    public List<ShelfTheme> getShelfThemeByNameStatusActiveAndInActive(String dbEnv, String themeName) {
        List<ShelfTheme> list = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            int statusActive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            int statusInactive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inactive").getLookupcode());
            List status = new ArrayList();
            status.add(statusActive);
            status.add(statusInactive);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTheme.class);
            if (null != themeName && !"".equals(themeName)) {
                criteria.add(Restrictions.eq("themeName", themeName));
            }
            criteria.add(Restrictions.in("status", status));
            criteria.addOrder(Order.asc("themeName"));
            list = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            logger.info(e.getMessage());
            if (null != trans) {
                trans.rollback();
            }
            throw e;
        }
        return list;
    }
}
