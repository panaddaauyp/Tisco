/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package th.co.d1.digitallending.dao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.JSONArray;
import org.json.JSONObject;
import th.co.d1.digitallending.entity.ShelfProduct;
import th.co.d1.digitallending.entity.ShelfProductDtl;
import th.co.d1.digitallending.entity.ShelfProductVcs;
import th.co.d1.digitallending.entity.ShelfTheme;
import th.co.d1.digitallending.util.DateUtils;
import th.co.d1.digitallending.util.HibernateUtil;
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.ValidUtils;

public class ShelfProductVcsDao {

    Logger logger = Logger.getLogger(ShelfProductVcsDao.class);
    private Session session;

    public List<ShelfProductVcs> getListShelfProductDtl(String dbEnv) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            cr.select(root);
            Query<ShelfProductVcs> prodDtlCmd = session.createQuery(cr);
            shelfProductVcsList = prodDtlCmd.getResultList();
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
        return shelfProductVcsList;
    }

    public ShelfProductVcs getShelfProductVcsByUUID(String dbEnv, String uuid) {
        ShelfProductVcs shelfProductVcs = new ShelfProductVcs();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            shelfProductVcs = (ShelfProductVcs) session.get(ShelfProductVcs.class, uuid);
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
        return shelfProductVcs;
    }

    public ShelfProductVcs createShelfProductVcs(String dbEnv, ShelfProductVcs shelfProductVcs) {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.save(shelfProductVcs);
            trans.commit();
            session.close();
            return shelfProductVcs;
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

    public ShelfProductVcs updateShelfProductVcs(String dbEnv, ShelfProductVcs shelfProductVcs) {
        Transaction trans = null;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            session.update(shelfProductVcs);
            trans.commit();
            session.close();
            return shelfProductVcs;
        } catch (HibernateException | NullPointerException e) {
            trans.rollback();
            logger.error("" + e);
            e.printStackTrace();
            return new ShelfProductVcs();
        } finally {
            if (null != session) {
                session.close();
            }
        }
    }

    public List<ShelfProductVcs> getListShelfProduct(String dbEnv, String productUUID, int status, int verProd) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            Join<ShelfProductVcs, ShelfProduct> joinObject = root.join("prodUuid");
            if (verProd > 0) {
                cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), productUUID), cb.equal(root.get("verProd"), verProd)));
            } else if (status > 0) {
                cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), productUUID), cb.equal(root.get("status"), status)));
            }
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            shelfProductVcsList = prodVcsCmd.list();
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
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatus(String dbEnv, String productUUID, int verProd, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans;
        try {
            List<Integer> refStatus = Arrays.asList(status);
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.eq("prodUuid.uuid", productUUID));
            criteria.add(Restrictions.eq("verProd", verProd));
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
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
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatus(String dbEnv, String productUUID, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans;
        try {
            List<Integer> refStatus = Arrays.asList(status);
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            if (null != productUUID && !"".equals(productUUID)) {
                criteria.add(Restrictions.eq("prodUuid.uuid", productUUID));
            }
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
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
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatus(String dbEnv, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans;
        try {
            List<Integer> refStatus = Arrays.asList(status);
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.isNull("compUuid.uuid"));
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
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
        return shelfProductVcsList;
    }

    public JSONArray getVCSComponent(String dbEnv, String prodUuid, String compCode) {
        JSONArray resp = new JSONArray();
        Transaction trans;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.createAlias("prodUuid", "prod");
            criteria.add(Restrictions.eq("prod.uuid", prodUuid));
            criteria.createAlias("compUuid", "comp");
            criteria.add(Restrictions.eq("comp.compCode", compCode));
            List<ShelfProductVcs> shelfProductVcsList = criteria.list();
            for (ShelfProductVcs vcs : shelfProductVcsList) {
                criteria = session.createCriteria(ShelfProductDtl.class);
                criteria.createAlias("trnUuid", "vcs");
//                criteria.createAlias("vcs.compUuid", "comp");
                criteria.createAlias("vcs.prodUuid", "prod");
                criteria.add(Restrictions.isNull("vcs.compUuid"));
                criteria.add(Restrictions.eq("prod.uuid", vcs.getProdUuid().getUuid()));
                criteria.add(Restrictions.eq("vcs.verProd", vcs.getVerProd()));
                criteria.add(Restrictions.eq("lkCode", "activeDate"));
                List<ShelfProductDtl> list = criteria.list();
                Date activeDate = null;
                if (null != list && list.size() > 0) {
                    activeDate = ValidUtils.str2Date(list.get(0).getLkValue(), "yyyy-MM-dd");
                }
                StatusUtils.Status status = null;
                if (vcs.getStatus() > 0) {
                    status = StatusUtils.getStatusByCode(dbEnv, ValidUtils.obj2String(vcs.getStatus()));
                }
                JSONObject data = new JSONObject()
                        .put("prodUuid", vcs.getProdUuid().getUuid())
                        .put("vcsUuid", vcs.getUuid())
                        .put("compId", vcs.getCompUuid().getUuid())
                        .put("compCode", vcs.getCompUuid().getCompCode())
                        .put("compVer", ValidUtils.null2NoData(vcs.getVerComp()))
                        .put("createdDate", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(vcs.getUpdateAt(), "dd/MM/yyyy"), DateUtils.getDisplayEnDate(vcs.getCreateAt(), "dd/MM/yyyy")))
                        .put("activeDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(activeDate, "dd/MM/yyyy")))
                        .put("status", null != status ? status.getStatusCode() : "")
                        .put("statusNameTh", null != status ? status.getStatusNameTh() : "")
                        .put("statusNameEn", null != status ? status.getStatusNameEn() : "");
                resp.put(data);
            }
            trans.commit();
            session.close();
        } catch (HibernateException | NullPointerException | ParseException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return resp;
    }

    public int getMaxVersionProduct(String dbEnv, String prodUuid) {
        int ver = 0;
        Transaction trans;
        try {
            session = HibernateUtil.getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.eq("prodUuid.uuid", prodUuid));
            criteria.setProjection(Projections.max("verProd"));
            List results = criteria.list();
            if (results.size() > 0) {
                ver = ValidUtils.obj2Int(results.get(0)) + 1;
            }
            trans.commit();
            session.close();
        } catch (HibernateException e) {
            logger.error("" + e);
            e.printStackTrace();
        } finally {
            if (null != session) {
                session.close();
            }
        }
        return ver;
    }

    public List<ShelfProductVcs> getProductByTmpUuidAndTmpVer(String dbEnv, String tmpUuid, int verTem) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        try {
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            cr.select(root).where(cb.and(cb.equal(root.get("temUuid"), tmpUuid), cb.equal(root.get("verTem"), verTem), cb.equal(root.get("status"), StatusUtils.getActive(dbEnv).getStatusCode())));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
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

    public void updateShelfProductVcs(String dbEnv, List<JSONObject> list, Date expireDate, Integer statusExpire) {
        Transaction trans;
        try {
            Date sysdate = new Date();
            session = getSessionMaster(dbEnv).openSession();
            trans = session.beginTransaction();
            for (JSONObject obj : list) {
                Criteria criteria = session.createCriteria(ShelfProductVcs.class);
                criteria.add(Restrictions.eq("prodUuid.uuid", obj.getString("uuid")));
                criteria.add(Restrictions.eq("verProd", ValidUtils.obj2Int(obj.get("ver"))));
                List<ShelfProductVcs> shelfProductVcsList = criteria.list();
                for (ShelfProductVcs vcs : shelfProductVcsList) {
                    Integer status = ValidUtils.obj2Integer(obj.get("status"));
                    vcs.setStatus(status);
                    vcs.setState(StatusUtils.setStatus(vcs.getState(), ValidUtils.obj2String(vcs.getStatus())));
                    vcs.setUpdateAt(sysdate);
                    if (null == vcs.getCompUuid() && statusExpire.equals(status)) {
                        List<ShelfProductDtl> dtls = vcs.getShelfProductDtlList();
                        for (ShelfProductDtl dtl : dtls) {
                            if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                dtl.setEndDate(expireDate);
                            }
                        }
                    }
                }
            }
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
    }

    public List<ShelfProductVcs> getProductByTmpUuidAndTmpVerNotCancelAndDelete(String dbEnv, String tmpUuid, int verTem) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        try {
            List status = new ArrayList<>();
            status.add(StatusUtils.getCancel(dbEnv).getStatusCode());
            status.add(StatusUtils.getDelete(dbEnv).getStatusCode());
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            cr.select(root).where(cb.and(cb.equal(root.get("temUuid"), tmpUuid), cb.equal(root.get("verTem"), verTem), cb.not(root.get("status").in(status))));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
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

    public List<ShelfProductVcs> getProductByThemeUuid(String dbEnv, String themeUuid) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        try {
            int statusCancel = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "cancel").getLookupcode());
            int statusDelete = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "delete").getLookupcode());
            List status = new ArrayList();
            status.add(statusCancel);
            status.add(statusDelete);
            session = getSessionMaster(dbEnv).openSession();
            Transaction trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            Join<ShelfProductVcs, ShelfTheme> joinObject = root.join("themeUuid");
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), themeUuid), cb.not(root.get("status").in(status))));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
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
}
