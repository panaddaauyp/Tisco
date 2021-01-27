/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
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

    private Session session;

    Logger logger = Logger.getLogger(ShelfThemeDao.class);

    public JSONArray getListShelfTmpTheme(String dbEnv, boolean onlyActive) {
        List<ShelfTheme> list = new ArrayList<>();
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        JSONArray jsonArr = new JSONArray();
        Transaction trans;
        try {
            int statusActive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            int statusInactive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inactive").getLookupcode());
            int statusCancel = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "cancel").getLookupcode());
            int statusDelete = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "delete").getLookupcode());
            List statusActiveInactive = new ArrayList();
            statusActiveInactive.add(statusActive);
            statusActiveInactive.add(statusInactive);
            List statusNotIn = new ArrayList();
            statusNotIn.add(statusCancel);
            statusNotIn.add(statusDelete);
            session = getSessionMaster(dbEnv).openSession();
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
                        .put("createAt", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(theme.getCreateAt(), "dd/MM/yyyy HH:mm")))
                        .put("updateAt", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(theme.getUpdateAt(), "dd/MM/yyyy HH:mm"), DateUtils.getDisplayEnDate(theme.getCreateAt(), "dd/MM/yyyy HH:mm")))
                        .put("data", (theme.getValue() == null ? "" : theme.getValue()))
                        .put("createBy", ValidUtils.null2NoData(theme.getCreateBy()))
                        .put("updateBy", ValidUtils.null2NoData(theme.getUpdateBy()));
                criteria = session.createCriteria(ShelfProductVcs.class);
                criteria.createAlias("themeUuid", "theme");
                criteria.add(Restrictions.eq("theme.uuid", theme.getUuid()));
                criteria.add(Restrictions.isNull("compUuid"));
                criteria.add(Restrictions.not(Restrictions.in("status", statusNotIn)));
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
                jsonObj.put("prodUsage", prodArr);
                jsonArr.put(jsonObj);
            }
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return jsonArr;
    }

    public JSONObject saveShelfTmpTheme(String dbEnv, ShelfTheme theme) {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            if (null == theme.getCreateAt()) {
                theme.setCreateAt(new Date());
            }
            session.save(theme);
            trans.commit();
            session.close();
            resp.put("status", true);
            resp.put("theme", theme);
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            resp.put("status", false);
            resp.put("description", "" + e);
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return resp;
    }

    public ShelfTheme getShelfTmpTheme(String dbEnv, String uuid) {
        ShelfTheme theme = new ShelfTheme();
        Transaction trans = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            theme = (ShelfTheme) session.get(ShelfTheme.class, uuid);
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

    public JSONObject updateShelfTmpTheme(String dbEnv, ShelfTheme theme) {
        JSONObject resp = new JSONObject();
        Transaction trans = null;
        try {
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            if (null == theme.getUpdateAt()) {
                theme.setUpdateAt(new Date());
            }
            session.update(theme);
            trans.commit();
            session.close();
            resp.put("status", true);
            resp.put("theme", theme);
        } catch (HibernateException | NullPointerException e) {
            logger.error("" + e);
            e.printStackTrace();
            if (null != trans) {
                trans.rollback();
            }
            resp.put("status", false);
            resp.put("description", "" + e);
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return resp;
    }

    public JSONArray searchShelfTheme(String dbEnv, String themeName, Integer status, Date createFrom, Date createTo, Date updateFrom, Date updateTo) {
        List<ShelfTheme> themes = new ArrayList<>();
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
                        .put("createAt", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(theme.getCreateAt(), "dd/MM/yyyy HH:mm")))
                        .put("updateAt", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(theme.getUpdateAt(), "dd/MM/yyyy HH:mm"), DateUtils.getDisplayEnDate(theme.getCreateAt(), "dd/MM/yyyy HH:mm")))
                        .put("data", (theme.getValue() == null ? "" : theme.getValue()))
                        .put("createBy", ValidUtils.null2NoData(theme.getCreateBy()))
                        .put("updateBy", ValidUtils.null2NoData(theme.getUpdateBy()));
                criteria = session.createCriteria(ShelfProductVcs.class);
                criteria.createAlias("themeUuid", "theme");
                criteria.add(Restrictions.eq("theme.uuid", theme.getUuid()));
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
                jsonObj.put("prodUsage", prodArr);
                jsonArr.put(jsonObj);
            }
//            trans.commit();
//            session.close();
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

    public List<ShelfTheme> getShelfThemeByNameStatusActiveAndInActive(String dbEnv, String themeName) {
        List<ShelfTheme> list = new ArrayList<>();
        Transaction trans = null;
        try {
            int statusActive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "active").getLookupcode());
            int statusInactive = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "inactive").getLookupcode());
            List status = new ArrayList();
            status.add(statusActive);
            status.add(statusInactive);
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfTheme.class);
            if (null != themeName && !"".equals(themeName)) {
                criteria.add(Restrictions.eq("themeName", themeName));
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
