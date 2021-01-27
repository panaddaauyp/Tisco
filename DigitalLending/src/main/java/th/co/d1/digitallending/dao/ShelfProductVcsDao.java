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
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
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
import static th.co.d1.digitallending.util.HibernateUtil.getSessionMaster;
import th.co.d1.digitallending.util.StatusUtils;
import th.co.d1.digitallending.util.ValidUtils;

public class ShelfProductVcsDao {

    Logger logger = Logger.getLogger(ShelfProductVcsDao.class.getName());

    public List<ShelfProductVcs> getListShelfProductDtl(String dbEnv) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            cr.select(root);
            Query<ShelfProductVcs> prodDtlCmd = session.createQuery(cr);
            shelfProductVcsList = prodDtlCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public ShelfProductVcs getShelfProductVcsByUUID(String dbEnv, String uuid) {
        ShelfProductVcs shelfProductVcs = new ShelfProductVcs();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            shelfProductVcs = (ShelfProductVcs) session.get(ShelfProductVcs.class, uuid);
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcs;
    }

    public ShelfProductVcs createShelfProductVcs(String dbEnv, ShelfProductVcs shelfProductVcs) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.save(shelfProductVcs);
            trans.commit();
            return shelfProductVcs;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

    public ShelfProductVcs updateShelfProductVcs(String dbEnv, ShelfProductVcs shelfProductVcs) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            session.update(shelfProductVcs);
            trans.commit();
            return shelfProductVcs;
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

    public List<ShelfProductVcs> getListShelfProduct(String dbEnv, String productUUID, int status, int verProd) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            Join<ShelfProductVcs, ShelfProduct> joinObject = root.join("prodUuid");
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), productUUID), cb.equal(root.get("verProd"), verProd)));
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), productUUID), cb.equal(root.get("status"), status)));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            shelfProductVcsList = prodVcsCmd.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatus(String dbEnv, String productUUID, int verProd, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<Integer> refStatus = Arrays.asList(status);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.eq("prodUuid.uuid", productUUID));
            criteria.add(Restrictions.eq("verProd", verProd));
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatus(String dbEnv, String productUUID, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<Integer> refStatus = Arrays.asList(status);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            if (null != productUUID && !"".equals(productUUID)) {
                criteria.add(Restrictions.eq("prodUuid.uuid", productUUID));
            }
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatus(String dbEnv, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<Integer> refStatus = Arrays.asList(status);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.isNull("compUuid.uuid"));
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public JSONArray getVCSComponent(String dbEnv, String prodUuid, String compCode) throws ParseException {
        JSONArray resp = new JSONArray();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.createAlias("prodUuid", "prod");
            criteria.add(Restrictions.eq("prod.uuid", prodUuid));
            criteria.createAlias("compUuid", "comp");
            criteria.add(Restrictions.eq("comp.compCode", compCode));
            criteria.addOrder(Order.asc("verComp"));
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
                StatusUtils.Status status = StatusUtils.getStatusByCode(dbEnv, ValidUtils.obj2String(vcs.getStatus()));
                JSONObject data = new JSONObject()
                        .put("prodUuid", vcs.getProdUuid().getUuid())
                        .put("vcsUuid", vcs.getUuid())
                        .put("compId", vcs.getCompUuid().getUuid())
                        .put("compCode", vcs.getCompUuid().getCompCode())
                        .put("compVer", ValidUtils.null2NoData(vcs.getVerComp()))
                        .put("createdDate", ValidUtils.null2Separator(DateUtils.getDisplayEnDate(vcs.getUpdateAt(), "yyyy-MM-dd"), DateUtils.getDisplayEnDate(vcs.getCreateAt(), "yyyy-MM-dd")))
                        .put("activeDate", ValidUtils.null2NoData(DateUtils.getDisplayEnDate(activeDate, "yyyy-MM-dd")))
                        .put("status", null != status ? status.getStatusCode() : "")
                        .put("statusNameTh", null != status ? status.getStatusNameTh() : "")
                        .put("statusNameEn", null != status ? status.getStatusNameEn() : "");
                resp.put(data);
            }
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return resp;
    }

    public int getMaxVersionProduct(String dbEnv, String prodUuid) {
        int ver = 0;
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.eq("prodUuid.uuid", prodUuid));
            criteria.setProjection(Projections.max("verProd"));
            List results = criteria.list();
            if (results.size() > 0) {
                ver = ValidUtils.obj2Int(results.get(0)) + 1;
            }
            trans.commit();
        } catch (HibernateException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return ver;
    }

    public List<ShelfProductVcs> getProductByTmpUuidAndTmpVer(String dbEnv, String tmpUuid, int verTem) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            cr.select(root).where(cb.and(cb.equal(root.get("temUuid"), tmpUuid), cb.equal(root.get("verTem"), verTem), cb.equal(root.get("status"), StatusUtils.getActive(dbEnv).getStatusCode())));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }

    public void updateShelfProductVcs(String dbEnv, List<JSONObject> list) {
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            Date sysdate = new Date();
            trans = session.beginTransaction();
            for (JSONObject obj : list) {
                Criteria criteria = session.createCriteria(ShelfProductVcs.class);
                criteria.add(Restrictions.eq("prodUuid.uuid", obj.getString("uuid")));
                criteria.add(Restrictions.eq("verProd", ValidUtils.obj2Int(obj.get("ver"))));
                List<ShelfProductVcs> shelfProductVcsList = criteria.list();
                for (ShelfProductVcs vcs : shelfProductVcsList) {
                    Integer status = ValidUtils.obj2Integer(obj.get("status"));
                    vcs.setStatus(status);
                    vcs.setState(obj.getString("state"));
                    vcs.setUpdateAt(sysdate);
                    if (obj.has("expireDate") && null == vcs.getCompUuid()) {
                        List<ShelfProductDtl> dtls = vcs.getShelfProductDtlList();
                        for (ShelfProductDtl dtl : dtls) {
                            if ("activeDate".equalsIgnoreCase(dtl.getLkCode())) {
                                Date endDate = (Date) obj.get("expireDate");
                                dtl.setEndDate(endDate);
                                session.save(dtl);
                            }
                        }
                    }
                    session.save(vcs);
                }
            }
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
    }

    public List<ShelfProductVcs> getProductByNotProductStatusTmpUuidAndTmpVer(String dbEnv, String tmpUuid, int verTem, List status) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
//            List status = new ArrayList<>();
//            status.add(StatusUtils.getCancel(dbEnv).getStatusCode());
//            status.add(StatusUtils.getDelete(dbEnv).getStatusCode());
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            cr.select(root).where(cb.and(cb.equal(root.get("temUuid"), tmpUuid), cb.equal(root.get("verTem"), verTem), cb.not(root.get("status").in(status))));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }

    public List<ShelfProductVcs> getProductByThemeUuid(String dbEnv, String themeUuid) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            int statusCancel = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "cancel").getLookupcode());
            int statusDelete = Integer.parseInt(new SysLookupDao().getMemLookupByValue(dbEnv, "delete").getLookupcode());
            List status = new ArrayList();
            status.add(statusCancel);
            status.add(statusDelete);
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            Join<ShelfProductVcs, ShelfTheme> joinObject = root.join("themeUuid");
            cr.select(root).where(cb.and(cb.equal(joinObject.get("uuid"), themeUuid), cb.not(root.get("status").in(status))));
            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.getResultList();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByNotStatus(String dbEnv, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<Integer> refStatus = Arrays.asList(status);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.createAlias("prodUuid", "prod");
            criteria.add(Restrictions.isNull("compUuid.uuid"));
            criteria.add(Restrictions.not(Restrictions.in("status", refStatus)));
            criteria.addOrder(Order.asc("prod.prodCode"));
            shelfProductVcsList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByStatusNotIn(String dbEnv, String productUUID, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<Integer> refStatus = Arrays.asList(status);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            if (null != productUUID && !"".equals(productUUID)) {
                criteria.add(Restrictions.eq("prodUuid.uuid", productUUID));
            }
            criteria.add(Restrictions.not(Restrictions.in("status", refStatus)));
            shelfProductVcsList = criteria.list();
            trans.commit();
//            session.close();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }

    public List<ShelfProductVcs> getProductByTmpUuid(String dbEnv, String tmpUuid, List prodStatus) {
        List<ShelfProductVcs> listVcs = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<ShelfProductVcs> cr = cb.createQuery(ShelfProductVcs.class);
            Root<ShelfProductVcs> root = cr.from(ShelfProductVcs.class);
            if (prodStatus != null) {
                cr.select(root).where(cb.and(cb.and(cb.equal(root.get("temUuid"), tmpUuid)), root.get("status").in(prodStatus)));
            } else {
                cr.select(root).where(cb.and(cb.equal(root.get("temUuid"), tmpUuid)));
            }

            Query<ShelfProductVcs> prodVcsCmd = session.createQuery(cr);
            listVcs = prodVcsCmd.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return listVcs;
    }

    public List<ShelfProductVcs> getListShelfProductVcsListByProductUuid(String dbEnv, String prodUuid) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.eq("prodUuid.uuid", prodUuid));
            criteria.add(Restrictions.isNull("compUuid.uuid"));
            criteria.addOrder(Order.asc("verProd"));
            shelfProductVcsList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }
    
    public List<ShelfProductVcs> getListProductVcsListByStatus(String dbEnv, String productUUID, Integer[] status) {
        List<ShelfProductVcs> shelfProductVcsList = new ArrayList<>();
        Transaction trans = null;
        try (Session session = getSessionMaster(dbEnv).openSession()) {
            List<Integer> refStatus = Arrays.asList(status);
            trans = session.beginTransaction();
            Criteria criteria = session.createCriteria(ShelfProductVcs.class);
            criteria.add(Restrictions.eq("prodUuid.uuid", productUUID));
            criteria.add(Restrictions.in("status", refStatus));
            shelfProductVcsList = criteria.list();
            trans.commit();
        } catch (HibernateException | NullPointerException e) {
            if (trans != null) {
                trans.rollback();
            }
            logger.info(e.getMessage());
            throw e;
        }
        return shelfProductVcsList;
    }
}
